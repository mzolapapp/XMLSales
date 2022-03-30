package org.RMQSales;

import org.RMQSales.entity.Checks;
import org.RMQSales.entity.MarkupsDiscounts;
import org.RMQSales.entity.Payments;
import org.RMQSales.entity.Positions;
import org.RMQSales.service.ChecksService;
import org.RMQSales.service.MarkupsDiscountsService;
import org.RMQSales.service.PaymentsService;
import org.RMQSales.service.PositionsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@SpringBootApplication
public class Application implements CommandLineRunner {

    static Logger log = LoggerFactory.getLogger(Application.class);

    ChecksService checkService;
    MarkupsDiscountsService markupsDiscountsService;
    PaymentsService paymentsService;
    PositionsService positionsService;

    @Autowired
    public void setCheckService(ChecksService checkService) {
        this.checkService = checkService;
    }

    @Autowired
    public void setMarkupsDiscountsService(MarkupsDiscountsService markupsDiscountsService) {
        this.markupsDiscountsService = markupsDiscountsService;
    }

    @Autowired
    public void setPaymentsService(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    @Autowired
    public void setPositionsService(PositionsService positionsService) {
        this.positionsService = positionsService;
    }

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        File logs = new File(File.separator + "home" + File.separator + "rmqSales" + File.separator + "logs" + File.separator + "logfile.log");
        logs.renameTo(new File(File.separator + "home" + File.separator + "rmqSales" + File.separator + "logs" + File.separator + "logfile" + '.' + format.format(d) + ".log"));

    }

    @Override
    public void run(String... args) {
        File folder = new File(File.separator + "home" + File.separator + "rmqSales" +
                File.separator + "messages");
        ArrayList<String> filenames = new ArrayList<>();

        for (String filename : folder.list()) {
            filenames.add(folder + File.separator + filename);
        }

        for (String filename : filenames) {
            Application.log.info("Обработка файла: " + filename);
            try {
                Checks check = new Checks();
                Document document = Jsoup.parse(readBufferedReader(filename), "", Parser.xmlParser());
                Element link = Jsoup.parse(document.getElementsByAttributeValue("name", "ПолноеИмяМД").text(), "", Parser.xmlParser());
                String documentType = link.text();
                if (documentType.equals("Документ.ЧекККМ")) {
                    //id
                    Document id = Jsoup.parse(document
                                    .getElementsByAttributeValue("name", "Ссылка").first().toString()
                            , ""
                            , Parser.xmlParser());
                    UUID check_id = UUID.fromString(id.text());
                    check.setId(check_id);

                    //Статус чека
                    check.setCheck_status(document.getElementsByAttributeValue("name", "СтатусЧекаККМ").text());

                    //ДатаВремя
                    check.setDate_time(parseDatetime(document.getElementsByAttributeValue("name", "ДатаВремя").text().replace('T', ' ')));

                    //id аптеки
                    Document pharmacy = Jsoup.parse(document
                                    .getElementsByAttributeValue("name", "Аптека").first().toString()
                            , ""
                            , Parser.xmlParser());
                    check.setDivision_id(UUID.fromString(pharmacy.getElementsByAttributeValue("xsi:type", "UUID").text()));

                    //Сумма документа
                    check.setDocument_sum(parseDouble(document.getElementsByAttributeValue("name", "СуммаДокумента").text()));

                    //Продавец
                    Document employee = Jsoup.parse(document
                                    .getElementsByAttributeValue("name", "Продавец").first().toString()
                            , ""
                            , Parser.xmlParser());
                    check.setEmployee_id(UUID.fromString(employee.getElementsByAttributeValue("xsi:type", "UUID").text()));

                    // Чек интернет магазина
                    check.setInternet_sale(Boolean.parseBoolean(document.getElementsByAttributeValue("name", "ЭтоЧекИМ").text()));

                    //Вид операции
                    check.setOperation_type(document.getElementsByAttributeValue("name", "ВидОперации").text());

                    //Код карты
                    check.setBonus_card_code(document.getElementsByAttributeValue("name", "мзКодКартыНачисленияБонусов").text());

                    //Проведен
                    check.setPosted(Boolean.parseBoolean(document.getElementsByAttributeValue("name", "Проведен").text()));

                    //Пометка удаления
                    check.setDelete_mark(Boolean.parseBoolean(document.getElementsByAttributeValue("name", "ПометкаУдаления").text()));

                    checkService.save(check);

                    Application.log.info("Сохранение чека: " + id.text());


                    //Позиции чека - ввиду особенности составления XML-файла составление производится из массива элементов
                    Document positionsDoc = Jsoup.parse(document.getElementsByAttributeValue("name", "Товары").toString(), "", Parser.xmlParser());
                    Elements positions = positionsDoc.getElementsByTag("row");
                    for (Element element : positions) {
                        Positions position = new Positions();
                        Elements elements = element.getElementsByTag("Value");
                        int check_link = parseInt(elements.get(0).text());
                        double price = parseDouble(elements.get(1).text());
                        double count = parseDouble(elements.get(2).text());
                        double sum = parseDouble(elements.get(3).text());
                        double sum_vat = parseDouble(elements.get(4).text());
                        String mz_sale_tech = elements.get(5).text();
                        double manual_discount_sum = parseDouble(elements.get(6).text());
                        double auto_discount_sum = parseDouble(elements.get(7).text());
                        double bonus_discount_sum = parseDouble(elements.get(8).text());
                        double mz_mpm_bonus_sum = parseDouble(elements.get(9).text());
                        String mz_sale_mech = elements.get(10).text();
                        String[] mz_customer_profit_str = elements.get(11).text().split(" ");
                        double mz_customer_profit;
                        if (mz_customer_profit_str.length != 0) {
                            mz_customer_profit = parseDouble(mz_customer_profit_str[0]);
                        } else {
                            mz_customer_profit = parseDouble(elements.get(11).text());
                        }
                        UUID nomenclature_ref = UUID.fromString(elements.get(13).text());
                        UUID nomenclature_info_ref = UUID.fromString(elements.get(19).text());
                        Application.log.info("Сохранение позиции: " +
                                "чек - " + id.text() + "позиция" + check_link);

                        position.setCheckId(check.getId());
                        position.setCheckLink(check_link);
                        position.setPrice(price);
                        position.setCount(count);
                        position.setSum(sum);
                        position.setSum_vat(sum_vat);
                        position.setMz_sale_tech(mz_sale_tech);
                        position.setManual_discount_sum(manual_discount_sum);
                        position.setAuto_discount_sum(auto_discount_sum);
                        position.setBonus_discount_sum(bonus_discount_sum);
                        position.setMz_mpm_bonus_sum(mz_mpm_bonus_sum);
                        position.setMz_sale_mech(mz_sale_mech);
                        position.setMz_customer_profit(mz_customer_profit);
                        position.setNomenclature_ref(nomenclature_ref);
                        position.setNomenclature_info_ref(nomenclature_info_ref);
                        positionsService.save(position);
                        elements.clear();
                    }
                    //Платежи - ввиду особенности составления XML-файла составление производится из массива элементов
                    Document paymentDocument = Jsoup.parse(document.getElementsByAttributeValue("name", "Оплата").toString(), "", Parser.xmlParser());
                    Elements paymentValues = paymentDocument.getElementsByTag("row");
                    for (Element element : paymentValues) {
                        Payments payments = new Payments();
                        Elements elements = element.getElementsByTag("value");
                        String payment_type = elements.get(0).text();
                        double sum = parseDouble(elements.get(1).text());
                        int payment_link = parseInt(elements.get(2).text());
                        Application.log.info("Сохранение способа оплаты: чек - " + id.text());
                        payments.setCheck_id(check);
                        payments.setPayment_type(payment_type);
                        payments.setSum(sum);
                        payments.setPayment_link(payment_link);

                        paymentsService.save(payments);

                    }

                    //СкидкиНаценки - ввиду особенности составления XML-файла составление производится из массива элементов
                    Document markupsDiscountsDocument = Jsoup.parse(document.getElementsByAttributeValue("name", "СкидкиНаценки").toString(), "", Parser.xmlParser());
                    Elements markupsDiscountsElements = markupsDiscountsDocument.getElementsByTag("row");
                    if (markupsDiscountsElements.size() > 0) {
                        for (Element element : markupsDiscountsElements) {
                            MarkupsDiscounts markupsDiscounts = new MarkupsDiscounts();
                            Elements elements = element.getElementsByTag("value");
                            int check_link = parseInt(elements.get(0).text());
                            double sum = parseDouble(elements.get(1).text());
                            UUID markup_discount_ref = UUID.fromString(elements
                                    .get(2)
                                    .getElementsByAttributeValue("xsi:type", "UUID")
                                    .text());
                            Application.log.info("Сохранение скидок и наценок на товар чек - " + id.text() + ", позиция - " + check_link);
                            markupsDiscounts.setCheck_id(check.getId());
                            markupsDiscounts.setPosition_id(positionsService.getByCheckIdAndCheckLink(check_id, check_link).getId());
                            markupsDiscounts.setCheck_link(check_link);
                            markupsDiscounts.setSum(sum);
                            markupsDiscounts.setMarkup_discount_ref(markup_discount_ref);
                            markupsDiscountsService.save(markupsDiscounts);
                        }
                    }
                } else {
                    File file = new File(filename);
                    log.info("Файл " + filename + " переносится в папку: substandardMessages " +
                            file.renameTo(new File(File.separator + "home" +
                                    File.separator + "rmqSales" +
                                    File.separator + "substandardMessages" +
                                    File.separator + file.getName())));
                }
            } catch (FileNotFoundException e) {
                Application.log.error("Файл " + filename + " не найден", e.fillInStackTrace());
            } catch (IOException | IllegalArgumentException | IllegalStateException e) {
                File file = new File(filename);
                file.renameTo(new File(File.separator + "home" +
                        File.separator + "rmqSales" +
                        File.separator + "substandardMessages" +
                        File.separator + file.getName()));
                Application.log.error("Ошибка чтения файла: ", e.fillInStackTrace());
            }
            File file = new File(filename);
            Application.log.info("Сохранение чека в базу данных прошло успешно: " + file.delete());
        }

    }

    private static String readBufferedReader(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        reader.close();

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    double parseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            return Double.parseDouble(strNumber.replace(",", "."));
        } else return 0;
    }

    int parseInt(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            return Integer.parseInt(strNumber);
        } else return 0;
    }

    static LocalDateTime parseDatetime(String s) {
        if (s.equals(""))
            return LocalDateTime.parse("1970-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        else return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    static UUID uuidFromString(String s) {
        if (s.equals(""))
            return UUID.fromString("00000000-0000-0000-0000-000000000000");
        else return UUID.fromString(s);
    }

}
