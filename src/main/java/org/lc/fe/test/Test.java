package org.lc.fe.test;

import com.alibaba.fastjson.JSON;
import org.lc.fe.exception.XlsxParseException;
import org.lc.fe.model.DynamicColumn;
import org.lc.fe.ExcelHelper;
import org.lc.fe.model.ImportData;
import org.lc.fe.model.XlsxFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) throws IOException, XlsxParseException {
        List<A> list = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            A a = new A();
            a.setId(i*1000000000L);
            a.setName("好家伙" + i);

            List<B> bs = new ArrayList<>();
            B b1 = new B();
            b1.setHobby("吃饭");
            b1.setId(123123L);
            bs.add(b1);
            B b2 = new B();
            b2.setHobby("睡觉");
            b2.setId(234234L);
            bs.add(b2);
            B b3 = new B();
            b3.setId(345345L);
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
            c7.setName("c2");
            cs3.add(c7);
            b1.setParents(cs);
            b2.setParents(cs2);
            b3.setParents(cs3);

            list.add(a);
        }
//        System.out.println(JSON.toJSON(list));
        DynamicColumn dynamicColumn = new DynamicColumn()
                .addColumn("爱好1", "爱好")
                .addColumn("爱好id1", "爱好id")
                .addColumn("称谓1", "称谓")
                .addColumn("爱好2", "爱好")
                .addColumn("爱好id2", "爱好id")
                .addColumn("称谓2", "称谓")
                .addColumn("爱好3", "爱好")
                .addColumn("爱好id3", "爱好id")
                .addColumn("称谓3", "称谓");
        long t1 = System.currentTimeMillis();
        XlsxFile xlsxFile = ExcelHelper.exportXlsx(list, A.class, dynamicColumn);
        long t2 = System.currentTimeMillis();
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\ChenLei\\Desktop\\导出.xlsx");
        xlsxFile.write(fileOutputStream);
        fileOutputStream.close();
        System.out.println("导出用时：" + (t2-t1));
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\ChenLei\\Desktop\\导出.xlsx");
        long t3 = System.currentTimeMillis();
        ImportData<A> importData = ExcelHelper.importXlsx(A.class, fileInputStream, dynamicColumn);
        fileInputStream.close();
        long t4 = System.currentTimeMillis();
        System.out.println("导入用时：" + (t4-t3));
        System.out.println(JSON.toJSONString(importData));
        XlsxFile xlsxFile1 = ExcelHelper.exportXlsx(importData.getInvalid(), A.class, dynamicColumn);
        FileOutputStream fileOutputStream1 = new FileOutputStream("C:\\Users\\ChenLei\\Desktop\\导出.xlsx");
        xlsxFile1.write(fileOutputStream1);
        fileOutputStream1.close();

    }

//    {
//        if (lastLevel instanceof List) {
//            Object computeKey = lastLevel;
//            Integer computeNum = compute.get(computeKey + "" + unitElement);
//            if(computeNum == null){
//                computeNum = 0;
//            }
//            if(computeNum < ((List) lastLevel).size()){
//                currentLevel = ((List) lastLevel).get(computeNum);
//            }else {
//                currentLevel = null;
//            }
//            compute.put(computeKey + "" + unitElement, computeNum + 1);
//        } else {
//            currentLevel = parentField.field.get(lastLevel);
//            if (currentLevel instanceof List) {
//                Integer computeNum = compute.get(currentLevel + "" + unitElement);
//                if(computeNum == null){
//                    computeNum = 0;
//                }
//                lastLevel = currentLevel;
//                if(computeNum >= ((List)currentLevel).size()){
//                    currentLevel = createGenericObjOfList(parentField.field);
//                    ((List)lastLevel).add(currentLevel);
//                }else {
//                    currentLevel = ((List)currentLevel).get(computeNum);
//                }
//                compute.put(lastLevel + "" + unitElement, computeNum + 1);
//            }
//        }
//        if (currentLevel == null) {
//            if (!Object.class.equals(parentField.subclass)) {
//                currentLevel = parentField.subclass.newInstance();
//                if(currentLevel instanceof List){
//                    Object newInstance = createGenericObjOfList(parentField.field);
//                    ((List) currentLevel).add(newInstance);
//                    compute.put(currentLevel + "" + unitElement, 1);
//                    parentField.field.set(lastLevel, currentLevel);
//                    lastLevel = currentLevel;
//                    currentLevel = newInstance;
//                }
//            }else{
//                currentLevel = parentField.field.getType().newInstance();
//            }
//        }else {
//            if(index == parents.size() - 1 && currentLevel instanceof List){
//                Integer computeNum = compute.get(currentLevel + "" + unitElement);
//                if(computeNum == null){
//                    computeNum = 0;
//                }
//                compute.put(currentLevel + "" + unitElement, computeNum + 1);
//                currentLevel = ((List)currentLevel).get(computeNum);
//            }
//        }
//        lastLevel = currentLevel;
//    }
}
