package org.lc.fe.test;

import com.alibaba.fastjson.JSON;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.lc.fe.exception.ColumnDuplicateException;
import org.lc.fe.exception.FieldValueMappingException;
import org.lc.fe.model.DynamicColumn;
import org.lc.fe.ExcelHelper;
import org.lc.fe.model.ImportData;
import org.lc.fe.model.XlsxFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

class Hello{
    public static void main(String[] args) {
        HashSet<Long> longs = new HashSet<>();
        longs.add(1L);
        longs.add(2L);
        longs.add(3L);
        Iterator<Long> iterator = longs.iterator();
        while (iterator.hasNext()){
            Long next = iterator.next();
            System.out.println(next);
        }
        Iterator<Long> iterator2 = longs.iterator();
        while (iterator2.hasNext()){
            Long next = iterator2.next();
            System.out.println(next);
        }
    }
}
public class Test {
    public static void main(String[] args) throws IOException, ColumnDuplicateException, FieldValueMappingException {
        List<A> list = new ArrayList<>();
        for (int i = 0; i < 1; i++){
            A a = new A();
            a.setId(i*1000000000L);
            a.setName("好家伙" + i);

            List<B> bs = new ArrayList<>();
            B b1 = new B();
            b1.setHobby("吃饭");
            bs.add(b1);
            B b2 = new B();
            b2.setHobby("睡觉");
            bs.add(b2);
            B b3 = new B();
            b3.setHobby("打豆豆");
            bs.add(b3);
            a.setHobbies(bs);

            List<C> cs = new ArrayList<>();
            C c1 = new C();
            c1.setName("a");
            cs.add(c1);
            C c2 = new C();
            c2.setName("b");
            cs.add(c2);

            List<C> cs2 = new ArrayList<>();
            C c3 = new C();
            c3.setName("a1");
            cs2.add(c3);
            C c4 = new C();
            c4.setName("b1");


            List<C> cs3 = new ArrayList<>();
            C c5 = new C();
            c5.setName("a2");
            cs3.add(c5);
            C c6 = new C();
            c6.setName("b2");
            cs3.add(c6);
            cs2.add(c4);
            C c7 = new C();
            c7.setName("c3");
            cs3.add(c7);
            b1.setParents(cs);
            b2.setParents(cs2);
            b3.setParents(cs3);

            list.add(a);
        }
//        System.out.println(JSON.toJSON(list));
        DynamicColumn dynamicColumn = new DynamicColumn()
                .addColumn("爱好1", "爱好")
                .addColumn("称谓1", "称谓")
                .addColumn("爱好2", "爱好")
                .addColumn("称谓2", "称谓")
                .addColumn("爱好3", "爱好")
                .addColumn("称谓3", "称谓");
        XlsxFile xlsxFile = ExcelHelper.exportXlsx(list, A.class, dynamicColumn);
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\ChenLei\\Desktop\\导出.xlsx");
        xlsxFile.write(fileOutputStream);

        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\ChenLei\\Desktop\\导出.xlsx");
        ImportData<A> importData = ExcelHelper.importXlsx(A.class, fileInputStream, dynamicColumn);
        System.out.println(JSON.toJSON(importData.getValid()));

    }
}
