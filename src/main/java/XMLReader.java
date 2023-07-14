

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLReader {

    public List<JobBean> readTestngXML(ExcelReader excelReader, String xmlInput, String xpathExpression, List<JobBean> jobList, String builName)  {
        try{
            InputStream inputStream = new ByteArrayInputStream(xmlInput.getBytes(StandardCharsets.UTF_8));
            Reader reader = new InputStreamReader(inputStream,"UTF-8");
            InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbbuilder = dbFactory.newDocumentBuilder();

            Document doc = dbbuilder.parse(is);
            doc.getDocumentElement().normalize();


            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList)xPath.compile(xpathExpression).evaluate(doc, XPathConstants.NODESET);

            for(int i=0;i<nodeList.getLength();i++){
                Node nNode = nodeList.item(i);

                if(nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element  eElementParent = (Element) nNode.getParentNode();
                    String []tempClassAndPackage = eElementParent.getAttribute("name").split("");
                    String className = tempClassAndPackage[tempClassAndPackage.length-1];
                    String packageName = eElementParent.getAttribute("name").replace("."+className,"").trim();

                    Element eElement = (Element) nNode;
                    String testName = eElement.getAttribute("name");
                    String status = formatStatus(eElement.getAttribute("status"));
                    String duration = eElement.getAttribute("duration-ms");
                    String startDate = DateUtils.formatDate(eElement.getAttribute("started-at"),"yyyy-MM-dd'T'HH:mm:ss'Z'","dd.MM.yyyy HH:mm");
                    String localIterationNumber = "-";
                    NodeList valueNodeList = eElement.getElementsByTagName("value");
                    for(int j=0;j<valueNodeList.getLength();j++){
                        Node valueNode = valueNodeList.item(j);
                        if(valueNode.getNodeType() == Node.ELEMENT_NODE){
                            Element valueElement = (Element) valueNode;
                            String elementValueText = valueElement.getTextContent();
                            for(String temp: elementValueText.split(",")){
                                if(temp.contains("IterationNumber=")){
                                    localIterationNumber = temp.replace("IterationNumber=","").replace("}","").trim();
                                }
                            }
                        }
                    }

                    boolean jobExistInjobslist = false;
                    for(JobBean listJob : jobList){
                        if(testName.equals(listJob.getTestName())){
                            String dataProvider = eElement.getAttribute("data-provider");
                            if(dataProvider == null){
                                jobExistInjobslist = true;
                            } else{
                                String iterationNumber = listJob.getIterationNumber();
                                if(iterationNumber.equalsIgnoreCase(localIterationNumber)){
                                    jobExistInjobslist = true;
                                }
                            }

                            if(jobExistInjobslist){
                                if(!listJob.getStatus().equalsIgnoreCase("Passed") && "Passed".equalsIgnoreCase(status)){
                                    listJob.setStatus("passed");
                                    listJob.setDuration(duration);
                                    listJob.setDate(startDate);
                                    listJob.setException("-");
                                    listJob.setExceptionMessage("-");
                                    listJob.setFailremessage("-");
                                    listJob.setFailurereason("-");
                                    listJob.setFailureDetails("-");
                                    listJob.setBackendComponent("-");
                                }
                                break;
                            }
                        }
                    }

                    if(!jobExistInjobslist){
                        JobBean job = new JobBean();
                        job.setName(builName);
                        job.setTestName(testName);
                        job.setClassName(className);
                        job.setPackageName(packageName);
                        job.setStatus(status);
                        job.setIterationNumber(localIterationNumber);
                        job.setDuration(duration);
                        job.setDate(startDate);
                        job.setModule(excelReader.getModuleSubModule(job.getClassName(),"Module"));
                        job.setSubModule(excelReader.getModuleSubModule(job.getClassName(),"SubModule"));
                        if("failed".equalsIgnoreCase(job.getStatus())){
                            Element exception = (Element) eElement.getElementsByTagName("exception").item(0);
                            if(exception != null){
                                job.setException(exception.getAttribute("class"));
                                Element message = (Element) exception.getElementsByTagName("message").item(0);
                                if(message != null){
                                    job.setExceptionMessage(formatExceptionMessage(message.getTextContent()));
                                }

                            }
                            job = setFailureDetails(job);

                        }
                        jobList.add(job);
                    }

                }
            }


        } catch (UnsupportedEncodingException | ParseException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return  jobList;
    }

    public List<JobBean> readTestngXML(String xmlInput,String XpathExpression, List<JobBean> jobList, String buildName){
        return  jobList;
    }

    public JobBean setFailureDetails(JobBean job){
        if(job.getExceptionMessage().contains(":::")){
            List<String> messageList = ListUtils.getListFromString(job.getExceptionMessage(),":::");
            job.setFailremessage(messageList.get(0));
            job.setFailureDetails(messageList.get(1));
        }

        if(ListUtils.getListFromString("com.csg.pb.tau.directnet.exceptions.Error500Exception",",").contains(job.getException())){
            job.setStatus("Skipped");
            job.setFailurereason("Envirnoment");
            job.setBackendComponent(extractStringByStartEnd(job.getExceptionMessage(),"",""));// need to complete the method
        }
        else if(job.getException().contains("java.lang.AssertionError")){
            if(job.getExceptionMessage().contains("but found [Error 500")){
                job.setStatus("Skipped");
                job.setFailurereason("Envirnoment");
                job.setBackendComponent("Unknown");
            }
        }
        else if(ListUtils.getListFromString("org.openqa.Selenium.ElementNotVisibleException,org.openqa.selenium.remote.UnreachableBrowserException,org.openqa.selenium.StaleElementReferenceException,org.openqa.selenium.WebDriverException,org.openqa.selenium.remote.sessionNotFoundException",",").contains(job.getException())){
                job.setFailureDetails("skipped");
                job.setFailurereason("Selenium Issue");
        }
        else if(ListUtils.getListFromString("java.lang.NullPointerException,java.lang.ArrayIndexOutOfBoundsException",",").contains(job.getException())){
                job.setStatus("skipped");
                job.setFailurereason("script Issue");
        }
        else if(job.getException().contains("org.openqa.selenium.TimeoutException")){
            if(job.getExceptionMessage().contains("Click failed: timeout")){
                job.setStatus("Skipped");
                job.setFailurereason("Environment");
            }
        }
        return job;
    }

    public static String extractStringByStartEnd(String inputString,String startString,String endString){
        String regex = startString + "(.*?)" + endString ;
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(inputString);
        if(m.find()){
            return m.group(1);
        }
        return "Unknown";
    }

    private String formatStatus(String status){
        if(status.equalsIgnoreCase("PASS")){
            return "Passed";
        }
        else if(status.equalsIgnoreCase("FAIL")){
            return "Failed";
        }
        else if(status.equalsIgnoreCase("SKIP")){
            return "Skipped";
        }
        else{
            return status;
        }
    }

    private String formatExceptionMessage(String message){
        try{
            message = message.trim();

            if(message.startsWith("{\"errorMessage\":")){
                message = message.replaceAll(" \\{\"errorMessage\":\"","");
                message = message.substring(0,message.indexOf("\""));
            }
            else if(message.contains("Build info")){
                message = message.substring(0,message.indexOf("Build info")).replaceAll("\n","");
            }
        } catch (Exception e){
            //
        }

        return message.replaceFirst("Message","");
    }

}
