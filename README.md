# E-599 MEDLINE 
Repository for the spring 2018 CSCI E-599 MEDLINE project team

Setup
=====

The file [setup.ipynb](https://nbviewer.jupyter.org/github/CS599-MEDLINE/e599_medline/blob/master/setup.ipynb) contains the setup insructions and code for the backend services on AWS:  DynamoDB, S3 buckets, and AWS Lambda setup
-----

The backend elements are running on AWS using the services AWS Lambda, DynamoDB, IAM, S3, EC2, Cloud Formation and CloudWatch. The installation and configuration of the DynamoDB tables and the S3 buckets has been performed with the boto3 library in Python 3.6. The IAM roles and policies, the AWS Lambda functions and the associated event triggers are created with a AWS Cloud Formation template. The entire backend can be installed from scratch using Python and boto3, first creating the database tables and S3 buckets (or using the ones in place, as “pubmedcentral_oa” is about 40 GB) and then launching the Cloud Formation template with boto3. Every step is provided with instructions in a Jupyter notebook setup.ipynb.

Currently all of the AWS services are running from a single account, and the AWS IAM service has been used to configure access for all team members, through a user Group and Role permissions. While the aspects of access management for our customer are out of scope for the project, a similar Group/Role approach can be taken after handover. The relevant sections of the Cloud Formation template would need adjusting for the Region and AccountID references.  As such, the code in this notebook will only work if run with the appropriate access to the account.

DynamoDB databases
----

The DynamoDB tables “demographics” and “demographics_meta”  are created with just the primary key elements, and throughput capacities. The remaining attributes are created populated by the Lambda functions. The tables are created in setup.ipynb using boto3. There are also a full set of maintenance functions in the Jupyter notebook [create_table.ipynb](https://nbviewer.jupyter.org/github/CS599-MEDLINE/e599_medline/blob/master/create_table.ipynb) (batch populate, inquiring about the tables, database queries, updating, deleting items, etc.).  

S3 buckets  
-----

The S3 bucket “pubmedcentral_oa” holds all of the XML files for the Open Access articles which are the sources of information in the database (these are .nxml files). The S3 bucket "pubminer_upload" contains the configuration files, such as the Cloud Formation template, the Sentence Miner application jar file, and the source packages for the AWS Lambda functions. Both of these S3 buckets are created using boto3 (in setup.ipynb).   

AWS Lambda functions and AWS Cloud Formation  
----

The Lambda functions can be setup using the an AWS Cloud Formation template. The template, as well as the Lambda function packages are created using Python 3.6 in the Jupyter notebook setup.ipynb. The notebook is used to create (and adjust if necessary) the .py files for the  Lambda functions and can then be used to upload them to S3. Similarly, the Cloud Formation template is created and uploaded to S3 from the notebook, and then the Cloud Formation stack is launched using boto3. 
 
Each Lambda function is described in a dedicated section, along with the trigger events, and the example ARN for the running platform. A brief overview of the Lambda functions follows:  

- “GetPMCUpdatesFromCSV” does the search, intersection of PMIDs and PMCIDs for the Open Access subset, and the initial population of items in the demographics and demographics_meta tables. 
- “DownloadPMC-OAFileToS3” does the download of files from PMC-OA and the upload to S3. 
- “TruncateTable” does the table truncation. The AWS Lambda package includes the BeautifulSoup4 and lxml packages, which are not included by default on Lambda. 
- The sentence extraction is performed on an EC2 instance, which is launched with a “user-data” script to perform the installation of the packages and necessary .jar file to run. The file “SentenceMinerOnEC2Instance” does these steps.   
- The function "UpdateStatsInDemographicsMeta" does a final update to the demographics_meta dynamodb table with the statistics of the latest update to the demographics table  

Roles and Policies  
-----   

Three AWS Roles are created from within the Cloud Formation template, along with their associated AWS Policy documents. They are:  

- The role “PM_lambda-dynamo-execution-role” which is for the Lambda functions which poll the dynamodb streams, and which write to the dynamodb tables and S3.   
- The role “PM_lambda_start_stop_ec2” which is for the sentence extraction Lambda function to poll the dynamodb stream, launch the EC2 instance, as well as write to the dynamodb tables and S3. 
- The role "PM_lambda_dynamodb_S3_role" is for the other Lambda functions which do not require permission to invoke functions or launch instances, but require permissions to write to Dynamodb or to Put to or Get from S3 buckets.  

The Policy documents are included in the Role creation template, as inline Policies.   

Testing the installation
-----

The file [setup.ipynb](https://nbviewer.jupyter.org/github/CS599-MEDLINE/e599_medline/blob/master/setup.ipynb) contains the JSON code for the test events of the individual Lambda functions. In addition, the set of BeautifulSoup tests can be run from the notebook to confirm that the table mining functions are working as expected.  


Note for Full Installation
-------

It should be noted that if the entire backend needs to be installed from scratch, the demographics table can be populated, if necessary, for the entire set of PMCIDs, using the Python function (since an AWS Lambda function GrabUpdatesPMCfromCSV of such magnitude would time out). Once the primary key items for “pmcid” are populated, the rest of the backend will automatically generate from the sequence of Lambda functions, triggered initially from the stream for the demographics dynamodb table.
