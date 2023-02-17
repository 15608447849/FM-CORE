package bottle.tuples;


import static javax.xml.bind.JAXBIntrospector.getValue;

/**
 * Created by Leeping on 2018/4/8.
 * email: 793065165@qq.com
 */

public class Tuple2<A,B> extends Tuple1<A> implements Tuple.IValue2<B> {
    public Tuple2(A a,B b) {
        super(new Object[]{a,b});
    }

    Tuple2(Object[] obj) {
        super(obj);
    }

    @SuppressWarnings("unchecked")
    @Override
    public B getValue1() {
        return  (B)getValue(1);
    }
}
