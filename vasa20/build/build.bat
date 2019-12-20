@echo off

set current_path=%cd%
cd ..
set root_path = %cd%
cd current_path

wsdl2java -p com.vmware.vim.vasa.v20 -p http://data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.xsd -p http://fault.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.fault.xsd -p http://vvol.data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.vvol.xsd -p http://statistics.data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.statistics.xsd -p http://compliance.policy.data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.policy.compliance.xsd -p http://policy.data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.policy.xsd -p http://provider.capability.policy.data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.policy.capability.provider.xsd -p http://types.capability.policy.data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.policy.capability.types.xsd -p http://placement.policy.data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.policy.placement.xsd -p http://vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.xsd -p http://data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.xsd -p http://profile.policy.data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.policy.profile.xsd -p http://capability.policy.data.vasa.vim.vmware.com/xsd=com.vmware.vim.vasa.v20.data.policy.capability.xsd -p http://com.vmware.vim.vasa/2.0=com.vmware.vim.vasa.v20 -p http://com.vmware.vim.vasa/2.0/xsd=com.vmware.vim.vasa.v20.xsd -encoding utf-8  -server -impl -b jws.xml -b binding.xml -fe jaxws21 -d %root_path%\src\  "Phase 13 vasaService (081514).wsdl"

cd root_path
mvn package