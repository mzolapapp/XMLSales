# XMLSales
Программа обрабатвает XML-файлы из заданной папки, и загружает данные из <br>
него в таблицы checks, positions, payments, marckups_discounts в базе данных PostgreSQL <br>
При возникновении ошибки обработки файл переносится в папку substandartMessages 
Обработанный файл удаляется<br>
Программа логируется при записи в каждую таблицу и при возникновении ошибок<br>
Программа запускается раз в десять минут через crontab комадной java -jar /home/rmqSales/SpringSales.jar
