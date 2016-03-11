# DbUnit Extractor

Export database data to xml DbUnit dataset.

Copy selected rows from SQL Console as DbUnit XML.
![](https://raw.githubusercontent.com/kTT/dbunit-extractor/master/dbunit-extractor.png)
Change SQL query to XML in-place.
![](https://raw.githubusercontent.com/kTT/dbunit-extractor/master/in-place.gif)

Inspired by [@rakk](https://github.com/rakk) web tool for generating DbUnit XML lines.

# Promo movie

[![DbUnit Extractor plugin for IntelliJ IDEA Ultimate](http://img.youtube.com/vi/YjBO2bImpvY/0.jpg)](http://www.youtube.com/watch?v=YjBO2bImpvY)

# Usage

**Copy query results**

1. Install plugin and set up database connection
2. Write SQL query and execute it
3. Select rows and columns you want to export
4. Right click on selection
5. Select "Copy to clipboard as DbUnit XML" from context menu
6. Paste result in your .xml file

**In-place convert**

1. Install plugin and set up database connection
2. Open xml file for dbunit sample data (it has to starts with "<dataset>")
3. Write query you want to convert
4. Select query, press ALT+ENTER and choose "Convert query to XML".

# Download

[Jetbrains DbUnit Extractor plugin page](https://plugins.jetbrains.com/plugin/7958?pr=idea)

# Known issues

* Schema name is empty when using "Copy to clipboard as DbUnit XML".

Please synchronize your database. Intellij doesn't return metadata if datasource isn't synchronized.

* "Only one table queries are supported" error when using jTds.

This driver doesn't return metadata by default (issue: https://sourceforge.net/p/jtds/bugs/546/). Please add **useCursors=true** parameter to your database connection url.

Example: `jdbc:jtds:sqlserver://localhost/db;instance=TEST;**useCursors=true**`

# License

This project is licensed under the MIT License.
