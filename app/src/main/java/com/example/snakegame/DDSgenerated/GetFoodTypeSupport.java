package com.example.snakegame.SnakeGame;

import com.zrdds.infrastructure.CDRDeserializer;
import com.zrdds.infrastructure.CDRSerializer;
import com.zrdds.infrastructure.TypeCode;
import com.zrdds.infrastructure.TypeCodeFactory;
import com.zrdds.infrastructure.TypeCodeImpl;
import com.zrdds.infrastructure.TypeCodeKind;
import com.zrdds.publication.DataWriter;
import com.zrdds.subscription.DataReader;
import com.zrdds.topic.TypeSupport;

import java.io.UnsupportedEncodingException;

public class GetFoodTypeSupport extends TypeSupport {
    private String type_name = "GetFood";
    private static TypeCodeImpl s_typeCode = null;
    private static GetFoodTypeSupport m_instance = new GetFoodTypeSupport();

    private final byte[] tmp_byte_obj = new byte[1];
    private final char[] tmp_char_obj = new char[1];
    private final short[] tmp_short_obj = new short[1];
    private final int[] tmp_int_obj = new int[1];
    private final long[] tmp_long_obj = new long[1];
    private final float[] tmp_float_obj = new float[1];
    private final double[] tmp_double_obj = new double[1];
    private final boolean[] tmp_boolean_obj = new boolean[1];

    
    private GetFoodTypeSupport(){}

    
    public static TypeSupport get_instance() { return m_instance; }

    public Object create_sampleI() {
        GetFood sample = new GetFood();
        return sample;
    }

    public void destroy_sampleI(Object sample) {

    }

    public int copy_sampleI(Object dst,Object src) {
        GetFood GetFoodDst = (GetFood)dst;
        GetFood GetFoodSrc = (GetFood)src;
        GetFoodDst.copy(GetFoodSrc);
        return 1;
    }

    public int print_sample(Object _sample) {
        if (_sample == null){
            System.out.println("NULL");
            return -1;
        }
        GetFood sample = (GetFood)_sample;
        System.out.println("sample.player_id:" + sample.player_id);
        System.out.println("sample.item_id:" + sample.item_id);
        System.out.println("sample.item_type: " + sample.item_type.ordinal());
        System.out.println("sample.x:" + sample.x);
        System.out.println("sample.y:" + sample.y);
        return 0;
    }

    public String get_type_name(){
        return this.type_name;
    }

    public int get_max_sizeI(){
        return 20;
    }

    public int get_max_key_sizeI(){
        return 20;
    }

    public boolean has_keyI(){
        return false;
    }

    public String get_keyhashI(Object sample, long cdr){
        return "-1";
    }

    public DataReader create_data_reader() {return new GetFoodDataReader();}

    public DataWriter create_data_writer() {return new GetFoodDataWriter();}

    public TypeCode get_inner_typecode(){
        TypeCode userTypeCode = get_typecode();
        if (userTypeCode == null) return null;
        return userTypeCode.get_impl();
    }

    public int get_sizeI(Object _sample,long cdr, int offset) throws UnsupportedEncodingException {
        int initialAlignment = offset;
        GetFood sample = (GetFood)_sample;
        offset += CDRSerializer.get_untype_size(4, offset);

        offset += CDRSerializer.get_untype_size(4, offset);

        offset += CDRSerializer.get_untype_size(4, offset);

        offset += CDRSerializer.get_untype_size(4, offset);

        offset += CDRSerializer.get_untype_size(4, offset);

        return offset - initialAlignment;
    }

    public int serializeI(Object _sample ,long cdr) {
         GetFood sample = (GetFood) _sample;

        if (!CDRSerializer.put_int(cdr, sample.player_id)){
            System.out.println("serialize sample.player_id failed.");
            return -2;
        }

        if (!CDRSerializer.put_int(cdr, sample.item_id)){
            System.out.println("serialize sample.item_id failed.");
            return -2;
        }

        if (!CDRSerializer.put_int(cdr,sample.item_type.ordinal())){
            System.out.println("serialize sample.item_type failed.");
            return -2;
        }

        if (!CDRSerializer.put_int(cdr, sample.x)){
            System.out.println("serialize sample.x failed.");
            return -2;
        }

        if (!CDRSerializer.put_int(cdr, sample.y)){
            System.out.println("serialize sample.y failed.");
            return -2;
        }

        return 0;
    }

    synchronized public int deserializeI(Object _sample, long cdr){
        GetFood sample = (GetFood) _sample;
        if (!CDRDeserializer.get_int_array(cdr, tmp_int_obj, 1)){
            System.out.println("deserialize sample.player_id failed.");
            return -2;
        }
        sample.player_id= tmp_int_obj[0];

        if (!CDRDeserializer.get_int_array(cdr, tmp_int_obj, 1)){
            System.out.println("deserialize sample.item_id failed.");
            return -2;
        }
        sample.item_id= tmp_int_obj[0];

        if (!CDRDeserializer.get_int_array(cdr, tmp_int_obj, 1)){
            System.out.println("deserialize sample.item_type failed.");
            return -2;
        }
        sample.item_type.ordinal(tmp_int_obj[0]);

        if (!CDRDeserializer.get_int_array(cdr, tmp_int_obj, 1)){
            System.out.println("deserialize sample.x failed.");
            return -2;
        }
        sample.x= tmp_int_obj[0];

        if (!CDRDeserializer.get_int_array(cdr, tmp_int_obj, 1)){
            System.out.println("deserialize sample.y failed.");
            return -2;
        }
        sample.y= tmp_int_obj[0];

        return 0;
    }

    public int get_key_sizeI(Object _sample,long cdr,int offset)throws UnsupportedEncodingException {
        int initialAlignment = offset;
        GetFood sample = (GetFood)_sample;
        offset += get_sizeI(sample, cdr, offset);
        return offset - initialAlignment;
    }

    public int serialize_keyI(Object _sample, long cdr){
        GetFood sample = (GetFood)_sample;
        return 0;
    }

    public int deserialize_keyI(Object _sample, long cdr) {
        GetFood sample = (GetFood)_sample;
        return 0;
    }

    public TypeCode get_typecode(){
        if (s_typeCode != null) {
            return s_typeCode;
        }
        TypeCodeFactory factory = TypeCodeFactory.get_instance();

        s_typeCode = factory.create_struct_TC("SnakeGame.GetFood");
        if (s_typeCode == null){
            System.out.println("create struct GetFood typecode failed.");
            return s_typeCode;
        }
        int ret = 0;
        TypeCodeImpl memberTc = new TypeCodeImpl();
        TypeCodeImpl eleTc = new TypeCodeImpl();

        memberTc = factory.get_primitive_TC(TypeCodeKind.DDS_TK_INT);
        if (memberTc == null){
            System.out.println("Get Member player_id TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            0,
            0,
            "player_id",
            memberTc,
            false,
            false);
        if (ret < 0)
        {
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }

        memberTc = factory.get_primitive_TC(TypeCodeKind.DDS_TK_INT);
        if (memberTc == null){
            System.out.println("Get Member item_id TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            1,
            1,
            "item_id",
            memberTc,
            false,
            false);
        if (ret < 0)
        {
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }

        memberTc = (TypeCodeImpl) ItemTypeTypeSupport.get_typecode();
        if (memberTc == null){
            System.out.println("Get Member item_type TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            2,
            2,
            "item_type",
            memberTc,
            false,
            false);
        if (ret < 0)
        {
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }

        memberTc = factory.get_primitive_TC(TypeCodeKind.DDS_TK_INT);
        if (memberTc == null){
            System.out.println("Get Member x TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            3,
            3,
            "x",
            memberTc,
            false,
            false);
        if (ret < 0)
        {
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }

        memberTc = factory.get_primitive_TC(TypeCodeKind.DDS_TK_INT);
        if (memberTc == null){
            System.out.println("Get Member y TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            4,
            4,
            "y",
            memberTc,
            false,
            false);
        if (ret < 0)
        {
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }

        return s_typeCode;
    }

}