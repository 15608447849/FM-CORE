package bottle.objectref;

import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

class ObjectPool {

    private long time = System.currentTimeMillis(); //长期未更新时间 清理所有 移除池

    private final ReentrantLock lock = new ReentrantLock();

    private final int maxNumber; // 对象池最大的大小 ,当超过这个数量 删除对象

    private final Vector<Object> objects =  new Vector<>(); //存放对象池中对象的向量

    ObjectPool(int maxNumber) {
        this.maxNumber = maxNumber;
    }

    /**
     * 放入一个空闲对象
     * 如果超过对象池大小限制, 丢弃
     */
    void putObject(Object obj){
        try{
            lock.lock();
            if (objects.size() > maxNumber){
                return;
            }
            objects.addElement(obj);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    /**
     * 取出一个空闲对象
     * 从列表删除
     */
    Object getObject(){
        Object obj = null;
        try{
            lock.lock();
            Enumeration enumerate = objects.elements();
            if (enumerate.hasMoreElements()) {
                obj = enumerate.nextElement();
                objects.removeElement(obj);
            }
            return obj;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return obj;
    }

    int currentNumber(){
        return objects.size();
    }

    /**
     * 检测长期未使用,是否清理所有并通知是否移除自己
     * ideaTime 超时时间 毫秒
     * true 移除自己
     */
    boolean checkSelf(long ideaTime){
        if (System.currentTimeMillis() - time > ideaTime) {
            try{
                lock.lock();
                Enumeration enumerate = objects.elements();
                while (enumerate.hasMoreElements()) {
                    // 从对象池向量中删除
                    objects.removeElement(enumerate.nextElement());
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
        return false;
    }



}
