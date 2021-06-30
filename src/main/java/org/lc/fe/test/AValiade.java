package org.lc.fe.test;

import org.lc.fe.ExcelDataValidator;
import org.lc.fe.model.InvalidInfo;
import org.springframework.stereotype.Component;

@Component
public class AValiade implements ExcelDataValidator<A> {
    @Override
    public void valid(A data, InvalidInfo invalidInfo) {
        if(data.getId() < 1){
            invalidInfo.addInvalid("id", "id太小了");
        }
    }
}
