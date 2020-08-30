package jdbc.define.session;

import java.security.InvalidParameterException;

/**
 * 事务级别
 */
public enum TransactionIsolationLevel {

    DEFAULT {
        public int toInt() {
            return 0;
        }
    },
    READ_UNCOMMITTED {
        public int toInt() {
            return 1;
        }
    },
    READ_COMMITTED {
        public int toInt() {
            return 2;
        }
    },
    REPEATABLE_READ {
        public int toInt() {
            return 4;
        }
    },
    SERIALIZABLE {
        public int toInt() {
            return 8;
        }
    };

    public abstract int toInt();

    public static TransactionIsolationLevel fromInt(int level) {
        TransactionIsolationLevel[] arr = TransactionIsolationLevel.values();
        int length = arr.length;
        for(int i = 0; i < length; ++i) {
            TransactionIsolationLevel lv = arr[i];
            if (lv.toInt() == level) {
                return lv;
            }
        }
        throw new InvalidParameterException("Invalid TransactionIsolationLevel , "+ level);
    }
}
