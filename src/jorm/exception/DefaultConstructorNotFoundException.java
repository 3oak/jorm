package jorm.exception;

public class DefaultConstructorNotFoundException extends Exception{
    private final String className;

    public DefaultConstructorNotFoundException(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "DefaultConstructorNotFoundException{" +
                "className='" + className + '\'' +
                '}';
    }
}
