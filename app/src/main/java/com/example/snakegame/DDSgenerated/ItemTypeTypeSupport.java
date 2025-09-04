package com.example.snakegame.SnakeGame;

import com.zrdds.infrastructure.TypeCodeFactory;
import com.zrdds.infrastructure.TypeCodeImpl;

public class ItemTypeTypeSupport{

    public static TypeCodeImpl get_typecode(){
        TypeCodeImpl s_typeCode = new TypeCodeImpl();
         s_typeCode = TypeCodeFactory.get_instance().create_enum_TC( "SnakeGame.ItemType");
        if(s_typeCode == null){
            System.out.println("create enum ItemType typecode failed.");
            return s_typeCode;
        }
        int ret = 0;
        ret = s_typeCode.add_member_to_enum(0, "APPLE", 0);
        if (ret < 0){
            System.out.println("add enum member APPLE failed"+ ret);
            TypeCodeFactory.get_instance().delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_enum(1, "GOOD_FOOD", 1);
        if (ret < 0){
            System.out.println("add enum member GOOD_FOOD failed"+ ret);
            TypeCodeFactory.get_instance().delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_enum(2, "BAD_FOOD", 2);
        if (ret < 0){
            System.out.println("add enum member BAD_FOOD failed"+ ret);
            TypeCodeFactory.get_instance().delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        return s_typeCode;
    }

    public static TypeCodeImpl get_inner_typecode(){
        TypeCodeImpl userTypeCode = get_typecode();
        if (userTypeCode == null) return null;
        return userTypeCode.get_impl();
    }

}