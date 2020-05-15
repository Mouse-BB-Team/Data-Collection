package pl.edu.agh.data_collection.utils;

public abstract class ParserDecorator<T, R> implements Parser<T, R> {

    protected Parser<T, R> wrappedParser;

    public ParserDecorator(Parser<T, R> wrappedParser) {
        this.wrappedParser = wrappedParser;
    }
}
