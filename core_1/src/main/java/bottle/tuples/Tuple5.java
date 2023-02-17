package bottle.tuples;



/**
 * Created by Leeping on 2018/4/8.
 * email: 793065165@qq.com
 */

public class Tuple5<A,B,C,D,E> extends Tuple4<A,B,C,D> implements Tuple.IValue5<E> {

    public Tuple5(A a, B b, C c,D d,E e) {
        super(new Object[]{a,b,c,d,e});
    }

    @SuppressWarnings("unchecked")
    @Override
    public E getValue4() {
        return (E)getValue(4);
    }
}
