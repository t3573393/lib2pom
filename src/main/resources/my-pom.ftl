<dependencies>
<#list resolveObjs as aFileObj>
<#if aFileObj.scope== 'compile'>
<dependency>
    <groupId>${aFileObj.groupId}</groupId>
    <artifactId>${aFileObj.artifactId}</artifactId>
    <version>${aFileObj.version}</version>
    <scope>${aFileObj.scope}</scope>
    <#if aFileObj.dependencis?has_content>
    <exclusions>
    <#list aFileObj.dependencis?values as aDependency>
    	<#if aDependency.needExclude>
    	<exclusion>
		    <groupId>${aDependency.groupId}</groupId>
		    <artifactId>${aDependency.artifactId}</artifactId>
		</exclusion>
		</#if>
    </#list>
	</exclusions>
    </#if>
</dependency>
<#elseif aFileObj.scope== 'system'>
<dependency>
    <groupId>${aFileObj.groupId}</groupId>
    <artifactId>${aFileObj.artifactId}</artifactId>
    <scope>${aFileObj.scope}</scope>
    <systemPath><#noparse>${basedir}</#noparse>/lib/${aFileObj.fileFullName}</systemPath>
</dependency>
</#if>
</#list>
<#list systemObjs as aFileObj>
<dependency>
    <groupId>unknown</groupId>
    <artifactId>${aFileObj.fileName}</artifactId>
    <scope>${aFileObj.scope}</scope>
    <systemPath><#noparse>${basedir}</#noparse>/lib/${aFileObj.fileFullName}</systemPath>
</dependency>
</#list>
</dependencies>