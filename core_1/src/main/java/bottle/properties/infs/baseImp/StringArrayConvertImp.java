package bottle.properties.infs.baseImp;



import bottle.properties.infs.FieldConvert;

import java.lang.reflect.Field;

public class StringArrayConvertImp implements FieldConvert {
    @Override
    public void setValue(Object holder, Field f, Object v) throws IllegalArgumentException, IllegalAccessException {

        String str = String.valueOf(v);
        if (str == null || str.length()==0){
            f.set(holder,new String[]{});
            return;
        }

        char firstChar = str.charAt (0) ;
        char lastChar = str.charAt (str.length ()-1) ;
        if (!(firstChar == '[' && lastChar == ']')){
            f.set(holder,new String[]{});
            return;
        }

        String[] arr = str.substring(1,str.length ()-1).split(",");
        f.set(holder, arr);
    }
}
