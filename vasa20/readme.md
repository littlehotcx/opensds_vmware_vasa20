# How to create VMware dependence by wsdl
## 1.download wsdl file to build directory 
e.g Phase 13 vasaService (081514).wsdl
## 2.download cxf-3.3x and extract it
## 3.add system environment variables
	a.add CXF_HOME to cxf extract directory (e.g D:\Program Files\apache-cxf-3.3.4\)
	b.add CLASSPATH to cxf lib directory (e.g D:\Program Files\apache-cxf-3.3.4\lib)
	c.add PATH to cxf bin directory (e.g D:\Program Files\apache-cxf-3.3.4\bin)
## 4.generate source code by command line in build directory
	wsdl2java -p com.vmware.vim.vasa.v20 -p http://data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.xsd -p http://fault.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.fault.xsd -p http://vvol.data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.vvol.xsd -p http://statistics.data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.statistics.xsd -p http://compliance.policy.data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.policy.compliance.xsd -p http://policy.data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.policy.xsd -p http://provider.capability.policy.data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.policy.capability.provider.xsd -p http://types.capability.policy.data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.policy.capability.types.xsd -p http://placement.policy.data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.policy.placement.xsd -p http://vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.xsd -p http://data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.xsd -p http://profile.policy.data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.policy.profile.xsd -p http://capability.policy.data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.policy.capability.xsd -p http://com.vmware.vim.vasa/2.0=com.vmware.vim.vasa.v20 -p http://com.vmware.vim.vasa/2.0/xsd=com.vmware.vim.vasa.v20.xsd -encoding utf-8  -server -impl -b jws.xml -b binding.xml -fe jaxws21 -d ..\src\  "Phase 13 vasaService (081514).wsdl"
## 5.build jar file by mvn
	mvn package
