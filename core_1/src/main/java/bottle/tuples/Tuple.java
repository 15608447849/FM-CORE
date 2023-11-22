package bottle.tuples;

import java.util.Arrays;

/**
 * Created by Leeping on 2018/4/8.
 * email: 793065165@qq.com
 */


public abstract class Tuple  {

    private final Object[] valueArray;

    Tuple(Object[] objects) {
        valueArray = objects;
    }

    public Object getValue(int pos){
        if (pos>=valueArray.length) throw new ArrayIndexOutOfBoundsException();
        return valueArray[pos];
    }

    public Object[] getValueArray() {
        return valueArray;
    }

    @Override
    public String toString() {
        return super.toString()+Arrays.toString(valueArray);
    }

    public interface IValue1<X> {
        X getValue0();
    }
    public interface IValue2<X> {
        X getValue1();
    }
    public interface IValue3<X> {
        X getValue2();
    }

    public interface IValue4<X> {
        X getValue3();
    }
    public interface IValue5<X> {
        X getValue4();
    }



}
