package pl.edu.agh.data_collection.utils;

public interface Parser<T, R> {
    R parse(T objectToParse);
}
