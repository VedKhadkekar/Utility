public class JobBean {

    private String name = "-";

    private String module = "-";
    private String subModule = "-";

    private String status = "No Run";

    private String failurereason = "-";
    private String failremessage = "-";
    private String failureDetails = "-";

    private String backendComponent = "-";

    private String exception = "-";
    private String exceptionMessage = "-";

    private String duration = "-";
    private String date = "-";
    private String iterationNumber = "-";

    private String packageName = "-";
    private String className = "-";
    private String testName = "-";


    public String getName() {
        return name;
    }

    public String getModule() {
        return module;
    }

    public String getSubModule() {
        return subModule;
    }

    public String getStatus() {
        return status;
    }

    public String getFailurereason() {
        return failurereason;
    }

    public String getFailremessage() {
        return failremessage;
    }

    public String getFailureDetails() {
        return failureDetails;
    }

    public String getBackendComponent() {
        return backendComponent;
    }

    public String getException() {
        return exception;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public String getDuration() {
        return duration;
    }

    public String getDate() {
        return date;
    }

    public String getIterationNumber() {
        return iterationNumber;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getTestName() {
        return testName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModule(String module) {
        if(!module.isEmpty()){
            this.module = module;
        }
    }

    public void setSubModule(String subModule) {
        if(!subModule.isEmpty()) {
            this.subModule = subModule;
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFailurereason(String failurereason) {
        this.failurereason = failurereason;
    }

    public void setFailremessage(String failremessage) {
        this.failremessage = failremessage;
    }

    public void setFailureDetails(String failureDetails) {
        this.failureDetails = failureDetails;
    }

    public void setBackendComponent(String backendComponent) {
        this.backendComponent = backendComponent;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setIterationNumber(String iterationNumber) {
        this.iterationNumber = iterationNumber;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    @Override
    public String toString(){
        return  "jobBean [nanme="+ name + ", status=" + status + "]";
    }
}
