package bottle.tuples;



/**
 * Created by Leeping on 2018/4/8.
 * email: 793065165@qq.com
 */

public class Tuple4<A,B,C,D> extends Tuple3<A,B,C> implements Tuple.IValue4<D> {

    public Tuple4(A a, B b, C c,D d) {
        super(new Object[]{a,b,c,d});
    }

    Tuple4(Object[] obj) {
        super(obj);
    }

    @SuppressWarnings("unchecked")
    @Override
    public D getValue3() {
        return (D)getValue(3);
    }
}
