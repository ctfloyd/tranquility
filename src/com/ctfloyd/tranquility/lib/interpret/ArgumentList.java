package com.ctfloyd.tranquility.lib.interpret;

import java.util.*;
import java.util.function.Consumer;

public class ArgumentList implements Iterable<Value> {

    private final List<Value> arguments = new ArrayList<>();

    public ArgumentList(List<Value> arguments) {
        if (arguments != null) {
            arguments.forEach(this::addArgument);
        }
    }

    public void addArgument(Value value) {
        if (value != null) {
            arguments.add(value);
        }
    }

    public ArgumentList subList(int startIndex, int endIndex) {
        ArgumentList argumentList = new ArgumentList(Collections.emptyList());
        for (int i = startIndex; i < endIndex; i++) {
            argumentList.addArgument(getArgumentAt(i));
        }
        return argumentList;
    }

    public boolean isEmpty() {
        return arguments.isEmpty();
    }

    public void prepend(Value value) {
        arguments.add(0, value);
    }

    public void overwriteArgumentAt(int index, Value value) {
        arguments.set(index, value);
    }

    public int size() {
        return arguments.size();
    }

    public Value getArgumentAt(int index) {
        return arguments.size() > index ? arguments.get(index) : Value.undefined();
    }

    public Value getFirstArgument() {
        return getArgumentAt(0);
    }

    public Value getFirstArgumentOr(Value value) {
        Value argument =  getFirstArgument();
        if (argument.isUndefined()) {
            return value;
        }
        return argument;
    }

    public Value getSecondArgument() {
        return getArgumentAt(1);
    }

    @Override
    public Iterator<Value> iterator() {
        return arguments.iterator();
    }

    @Override
    public void forEach(Consumer<? super Value> action) {
        arguments.forEach(action);
    }

    @Override
    public Spliterator<Value> spliterator() {
        return arguments.spliterator();
    }
}
