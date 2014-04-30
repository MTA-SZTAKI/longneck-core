longneck-core
=============

Longneck data integration tool core library.

Longneck solves data transformation problems to enable efficient data integration for data warehousing or other 
transformation-intensive data cleaning and ETL activities. 

Longneck provides:

- simple and efficient data- and computational model,
- an extensible and flexible XML-based transformation language,
- modular and reusable transformation descriptors,
- a fast, scalable and robust Java execution engine for both multithreaded single-machine and distributed applications.

Core longneck functionality is extended with extensions, a transformation rule repository and examples, which include
- [longneck-weblog extension](https://github.com/MTA-SZTAKI/longneck-weblog) for reading and parsing webserver log files,
- [weblog-demo application](https://github.com/MTA-SZTAKI/weblog-demo), an example application to feed web analytics applications,
- [longneck-cdv extension](https://github.com/MTA-SZTAKI/longneck-cdv) to check verification digits,
- [longneck-app-seed](https://github.com/MTA-SZTAKI/longneck-app-seed) to provide a seed application which is easy to start with, and
can be used to process CSV files, copy tables etc. with ease
- [longneck-content-repo](https://github.com/MTA-SZTAKI/longneck-content-repo), a transformation rule repository, providing basic 
blocks and entities to start with,
- [longneck-dns extension](https://github.com/MTA-SZTAKI/longneck-dns), reverse DNS, to get the domain name for IP addresses,
- [longneck-lookup extension](https://github.com/MTA-SZTAKI/longneck-lookup) to enable dictionary lookups,
- [longneck-bdb extension](https://github.com/MTA-SZTAKI/longneck-bdb) for persistent key-value store-based dictionaries, eg.
to track cookies for unique visitor identification in webserver logs, or to cache domain names for IPs,
- [longneck-lookup extension](https://github.com/MTA-SZTAKI/longneck-lookup) to enable dictionary lookups.

See http://longneck.sztaki.hu for more details.


