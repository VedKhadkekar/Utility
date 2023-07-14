
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;


public class ListUtils {
    public  ListUtils(){

    }

    public  static List<String> getListFromString(String str,String delimiter){
        return Lists.newArrayList(Splitter.on(delimiter).trimResults().omitEmptyStrings().split(str));
    }

}
