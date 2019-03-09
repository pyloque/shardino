package codehole.shardino;

public class Holder<T> {

    private T value;

    public Holder() {}

    public Holder(T value) {
        this.value = value;
    }

    public void value(T value) {
        this.value = value;
    }

    public T value() {
        return value;
    }

}
