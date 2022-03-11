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
                Document document = Jsoup.parse(readBufferedReader(filename), "", Parser.xmlParser());
                //Получение ссылки
                Document link = Jsoup.parse(document.getElementsByAttributeValue("name", "Ссылка").first().toString(), "", Parser.xmlParser());
                String id = link.getElementsByAttributeValue("xsi:type", "d5p1:DocumentRef.ЧекККМ").text();
                //Статус чека
                String check_status = document.getElementsByAttributeValue("xsi:type", "d5p1:EnumRef.СтатусыЧековККМ").text();
                //ДатаВремя
                String date_time = document.getElementsByAttributeValue("xsi:type", "xs:dateTime").text().replace('T', ' ');
                //id аптеки
                Document pharmacy = Jsoup.parse(document.getElementsByAttributeValue("name", "Аптека").first().toString(), "", Parser.xmlParser());
                String division_id = pharmacy.getElementsByAttributeValue("xsi:type", "UUID").text();
                //Сумма документа
                Document sumDocument = Jsoup.parse(document.getElementsByAttributeValue("name", "СуммаДокумента").first().toString(), "", Parser.xmlParser());
                double document_sum = parseDouble(sumDocument.getElementsByAttributeValue("xsi:type", "xs:decimal").text());
                //Продавец
                Document employee = Jsoup.parse(document.getElementsByAttributeValue("name", "Продавец").first().toString(), "", Parser.xmlParser());
                String employee_id = employee.getElementsByAttributeValue("xsi:type", "UUID").text();
                // Чек интернет магазина
                Document checkIS = Jsoup.parse(document.getElementsByAttributeValue("name", "ЭтоЧекИМ").first().toString(), "", Parser.xmlParser());
                boolean internet_sale = Boolean.parseBoolean(checkIS.getElementsByAttributeValue("xsi:type", "xs:boolean").text());
                //Вид операции
                String operation_type = document.getElementsByAttributeValue("xsi:type", "d5p1:EnumRef.ВидыОперацийЧекККМ").text();
                //Код карты
                Document bonusCard = Jsoup.parse(document.getElementsByAttributeValue("name", "мзКодКартыНачисленияБонусов").first().toString(), "", Parser.xmlParser());
                String bonus_card_code = bonusCard.getElementsByAttributeValue("xsi:type", "xs:string").text();
                Checks check = new Checks();
                check.setId(UUID.fromString(id));
                check.setCheck_status(check_status);
                check.setDate_time(LocalDateTime.parse(date_time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                check.setDivision_id(UUID.fromString(division_id));
                check.setDocument_sum(document_sum);
                check.setEmployee_id(UUID.fromString(employee_id));
                check.setInternet_sale(internet_sale);
                check.setOperation_type(operation_type);
                check.setBonus_card_code(bonus_card_code);
                check.setInserted_at(LocalDateTime.now());
                check.setUpdated_at(LocalDateTime.now());
                checkService.save(check);

                Application.log.info("Сохранение чека: " + id);
                Application.log.info("Сохранение чека: " + id);


                //Позиции чека
                Document position = Jsoup.parse(document.getElementsByAttributeValue("name", "Товары").toString(), "", Parser.xmlParser());
                Elements positions = position.getElementsByTag("row");
                for (Element element : positions) {
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
                            "чек - " + id + "позиция" + check_link);

                    if (!positionsService.getByCheckIdAndCheckLink(check, check_link).isEmpty()) {
                        Positions positionObject = positionsService.getByCheckIdAndCheckLink(check, check_link).get(0);
                        positionObject.setPrice(price);
                        positionObject.setCount(count);
                        positionObject.setSum(sum);
                        positionObject.setSum_vat(sum_vat);
                        positionObject.setMz_sale_tech(mz_sale_tech);
                        positionObject.setManual_discount_sum(manual_discount_sum);
                        positionObject.setAuto_discount_sum(auto_discount_sum);
                        positionObject.setBonus_discount_sum(bonus_discount_sum);
                        positionObject.setMz_mpm_bonus_sum(mz_mpm_bonus_sum);
                        positionObject.setMz_sale_mech(mz_sale_mech);
                        positionObject.setMz_customer_profit(mz_customer_profit);
                        positionObject.setNomenclature_ref(nomenclature_ref);
                        positionObject.setNomenclature_info_ref(nomenclature_info_ref);
                        positionObject.setUpdated_at(LocalDateTime.now());

                        positionsService.save(positionObject);
                    } else {
                        Positions positionObject = new Positions();
                        positionObject.setCheckId(check);
                        positionObject.setCheckLink(check_link);
                        positionObject.setPrice(price);
                        positionObject.setCount(count);
                        positionObject.setSum(sum);
                        positionObject.setSum_vat(sum_vat);
                        positionObject.setMz_sale_tech(mz_sale_tech);
                        positionObject.setManual_discount_sum(manual_discount_sum);
                        positionObject.setAuto_discount_sum(auto_discount_sum);
                        positionObject.setBonus_discount_sum(bonus_discount_sum);
                        positionObject.setMz_mpm_bonus_sum(mz_mpm_bonus_sum);
                        positionObject.setMz_sale_mech(mz_sale_mech);
                        positionObject.setMz_customer_profit(mz_customer_profit);
                        positionObject.setNomenclature_ref(nomenclature_ref);
                        positionObject.setNomenclature_info_ref(nomenclature_info_ref);
                        positionObject.setInserted_at(LocalDateTime.now());
                        positionObject.setUpdated_at(LocalDateTime.now());

                        positionsService.save(positionObject);
                    }
                    elements.clear();
                }
                //Платежи
                Document paymentDocument = Jsoup.parse(document.getElementsByAttributeValue("name", "Оплата").toString(), "", Parser.xmlParser());
                Elements paymentValues = paymentDocument.getElementsByTag("row");
                for (Element element : paymentValues) {
                    Elements elements = element.getElementsByTag("value");
                    String payment_type = elements.get(0).text();
                    double sum = parseDouble(elements.get(1).text());
                    int payment_link = parseInt(elements.get(2).text());
                    Application.log.info("Сохранение способа оплаты: чек - " + id);
                    Payments payments = new Payments();
                    payments.setCheck_id(check);
                    payments.setPayment_type(payment_type);
                    payments.setSum(sum);
                    payments.setPayment_link(payment_link);
                    payments.setInserted_at(LocalDateTime.now());
                    payments.setUpdated_at(LocalDateTime.now());

                    paymentsService.save(payments);

                }

                //СкидкиНаценки
                Document markupsDiscountsDocument = Jsoup.parse(document.getElementsByAttributeValue("name", "СкидкиНаценки").toString(), "", Parser.xmlParser());
                Elements markupsDiscountsElements = markupsDiscountsDocument.getElementsByTag("row");
                if (markupsDiscountsElements.size() > 0) {
                    for (Element element : markupsDiscountsElements) {
                        Elements elements = element.getElementsByTag("value");
                        int check_link = parseInt(elements.get(0).text());
                        double sum = parseDouble(elements.get(1).text());
                        UUID markup_discount_ref = UUID.fromString(elements
                                .get(2)
                                .getElementsByAttributeValue("xsi:type", "UUID")
                                .text());
                        Application.log.info("Сохранение скидок и наценок на товар чек - " + id + ", позиция - " + check_link);
                        MarkupsDiscounts markupsDiscounts = new MarkupsDiscounts();
                        markupsDiscounts.setCheck_id(check);
                        markupsDiscounts.setPosition_id(
                                positionsService.getByCheckIdAndCheckLink(check, check_link).get(0)
                        );
                        markupsDiscounts.setCheck_link(check_link);
                        markupsDiscounts.setSum(sum);
                        markupsDiscounts.setMarkup_discount_ref(markup_discount_ref);
                        markupsDiscounts.setInserted_at(LocalDateTime.now());
                        markupsDiscounts.setUpdated_at(LocalDateTime.now());

                        markupsDiscountsService.save(markupsDiscounts);
                    }
                }
            } catch (FileNotFoundException e) {
                Application.log.error("Файл " + filename + " не найден", e.fillInStackTrace());
            } catch (IOException | IndexOutOfBoundsException | IllegalArgumentException e) {
                Application.log.error("Ошибка чтения файла", e.fillInStackTrace());
                File file = new File(filename);
                file.renameTo(new File(File.separator + "home" +
                        File.separator + "rmqSales" +
                        File.separator + "substandardMessages" +
                        File.separator + file.getName()));
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


}
