package jorm.exception;

@SuppressWarnings("unused")
public class InvalidSchemaException extends Exception {
    private final String annotationName;
    private final String className;

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
