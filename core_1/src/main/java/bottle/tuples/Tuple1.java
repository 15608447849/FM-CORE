package bottle.tuples;



/**
 * Created by Leeping on 2018/4/8.
 * email: 793065165@qq.com
 */

public class Tuple1<A> extends Tuple implements Tuple.IValue1<A> {
    public Tuple1(A a) {
        super(new Object[]{a});
    }

    Tuple1(Object[] obj) {
        super(obj);
    }
    @SuppressWarnings("unchecked")
    @Override
    public A getValue0() {
        return (A)getValue(0);
    }

}
