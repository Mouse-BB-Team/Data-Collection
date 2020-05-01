package pl.edu.agh.data_collection.utils;

import java.text.ParseException;

public interface Parser<T, R>{
    R parse(T arg) throws ParseException;
}
