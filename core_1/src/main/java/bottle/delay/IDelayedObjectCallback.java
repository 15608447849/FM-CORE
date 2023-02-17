package bottle.delay;

/**
 * @Author: leeping
 * @Date: 2020/7/7 13:38
 */
public interface IDelayedObjectCallback<T extends IDelayedObject> {
     boolean  handlerCall(T iDelayedObject);
}
