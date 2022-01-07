package jorm.exception;

public class InvalidSchemaException extends Exception {
    private String annotationName;
    private String className;

    public InvalidSchemaException(String annotationName, String className) {
        this.annotationName = annotationName;
        this.className = className;
    }


    @Override
    public String toString() {
        return "InvalidSchemaException{" +
                "annotationName='" + annotationName + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}
