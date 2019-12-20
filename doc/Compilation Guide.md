# VASA Provider Compilation Guide
## 1.create VMware dependence by wsdl
see vasa20/readme.md
##2.package vasa20.jar
    1.mvn package 
    2.copy vasa20.jar to libs directory
##3.package platform 
	1.mvn package 
##4.package vasa.jar
	change directory to VASA_Provider_2.0
	mvn package