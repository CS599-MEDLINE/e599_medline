# Notes on Retrieving Abstract Data From PubMed

## By URL
An individual abstract can be retrieved by url.  E.g.:

https://www.ncbi.nlm.nih.gov/pubmed/29327980?report=xml&format=text<br/>

The above url retrieves the abstract in XML format.  It is also possible to retrieve the document in MEDLINE format:<br/>

https://www.ncbi.nlm.nih.gov/pubmed/29327980?report=medline&format=text

The number after pubmed/ is the PMID.  These appear to start at 1 and increment sequentially. It doesn't look like it would be hard to write a program to generate these urls and parse the results.

In informal testing, time to retrieve a document in this way was around 500ms.  To retrieve 28,000,000 individual documents would take 324 days.  That just isn't feasible.

The search feature of PubMed will return a larger result set. Unfortunately, it looks like exporting the set only captures the first page (20 records) of the results.

## Entrez Programming Utilities (E-Utilities)

https://www.ncbi.nlm.nih.gov/books/NBK25497/

A more sophisticated set of tools for querying the database using a url query syntax. The tools run on a dedicated server, and probably give much better performance than the method listed above.  There is a limit of 3 requests per second.

More research is needed.

## By FTP

The entire database can be retrieved from the PubMed FTP site as a set of 928 compressed files each containing around 30,000 entries formatted as XML data. The PubMed Help page says the data is available for free for large scale data mining.

https://www.ncbi.nlm.nih.gov/books/NBK3827/#pubmedhelp.PubMed_Quick_Start

The baseline database is updated once per year (in December) and updates are published daily.

There are terms and conditions governing the use of the data.

https://www.nlm.nih.gov/databases/download/terms_and_conditions.html
