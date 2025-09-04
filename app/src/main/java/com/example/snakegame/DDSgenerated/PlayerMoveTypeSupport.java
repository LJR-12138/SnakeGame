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

public class PlayerMoveTypeSupport extends TypeSupport {
    private String type_name = "PlayerMove";
    private static TypeCodeImpl s_typeCode = null;
    private static PlayerMoveTypeSupport m_instance = new PlayerMoveTypeSupport();

    private final byte[] tmp_byte_obj = new byte[1];
    private final char[] tmp_char_obj = new char[1];
    private final short[] tmp_short_obj = new short[1];
    private final int[] tmp_int_obj = new int[1];
    private final long[] tmp_long_obj = new long[1];
    private final float[] tmp_float_obj = new float[1];
    private final double[] tmp_double_obj = new double[1];
    private final boolean[] tmp_boolean_obj = new boolean[1];

    
    private PlayerMoveTypeSupport(){}

    
    public static TypeSupport get_instance() { return m_instance; }

    public Object create_sampleI() {
        PlayerMove sample = new PlayerMove();
        return sample;
    }

    public void destroy_sampleI(Object sample) {

    }

    public int copy_sampleI(Object dst,Object src) {
        PlayerMove PlayerMoveDst = (PlayerMove)dst;
        PlayerMove PlayerMoveSrc = (PlayerMove)src;
        PlayerMoveDst.copy(PlayerMoveSrc);
        return 1;
    }

    public int print_sample(Object _sample) {
        if (_sample == null){
            System.out.println("NULL");
            return -1;
        }
        PlayerMove sample = (PlayerMove)_sample;
        System.out.println("sample.player_id:" + sample.player_id);
        if (sample.direction != null){
            System.out.println("sample.direction:" + sample.direction);
        }
        else{
            System.out.println("sample.direction: null");
        }
        System.out.println("sample.timestamp:" + sample.timestamp);
        return 0;
    }

    public String get_type_name(){
        return this.type_name;
    }

    public int get_max_sizeI(){
        return 268;
    }

    public int get_max_key_sizeI(){
        return 268;
    }

    public boolean has_keyI(){
        return false;
    }

    public String get_keyhashI(Object sample, long cdr){
        return "-1";
    }

    public DataReader create_data_reader() {return new PlayerMoveDataReader();}

    public DataWriter create_data_writer() {return new PlayerMoveDataWriter();}

    public TypeCode get_inner_typecode(){
        TypeCode userTypeCode = get_typecode();
        if (userTypeCode == null) return null;
        return userTypeCode.get_impl();
    }

    public int get_sizeI(Object _sample,long cdr, int offset) throws UnsupportedEncodingException {
        int initialAlignment = offset;
        PlayerMove sample = (PlayerMove)_sample;
        offset += CDRSerializer.get_untype_size(4, offset);

        offset += CDRSerializer.get_string_size(sample.direction == null ? 0 : sample.direction.getBytes().length, offset);

        offset += CDRSerializer.get_untype_size(4, offset);

        return offset - initialAlignment;
    }

    public int serializeI(Object _sample ,long cdr) {
         PlayerMove sample = (PlayerMove) _sample;

        if (!CDRSerializer.put_int(cdr, sample.player_id)){
            System.out.println("serialize sample.player_id failed.");
            return -2;
        }

        if (!CDRSerializer.put_string(cdr, sample.direction, sample.direction == null ? 0 : sample.direction.length())){
            System.out.println("serialize sample.direction failed.");
            return -2;
        }

        if (!CDRSerializer.put_int(cdr, sample.timestamp)){
            System.out.println("serialize sample.timestamp failed.");
            return -2;
        }

        return 0;
    }

    synchronized public int deserializeI(Object _sample, long cdr){
        PlayerMove sample = (PlayerMove) _sample;
        if (!CDRDeserializer.get_int_array(cdr, tmp_int_obj, 1)){
            System.out.println("deserialize sample.player_id failed.");
            return -2;
        }
        sample.player_id= tmp_int_obj[0];

        sample.direction = CDRDeserializer.get_string(cdr);
        if(sample.direction ==null){
            System.out.println("deserialize member sample.direction failed.");
            return -3;
        }

        if (!CDRDeserializer.get_int_array(cdr, tmp_int_obj, 1)){
            System.out.println("deserialize sample.timestamp failed.");
            return -2;
        }
        sample.timestamp= tmp_int_obj[0];

        return 0;
    }

    public int get_key_sizeI(Object _sample,long cdr,int offset)throws UnsupportedEncodingException {
        int initialAlignment = offset;
        PlayerMove sample = (PlayerMove)_sample;
        offset += get_sizeI(sample, cdr, offset);
        return offset - initialAlignment;
    }

    public int serialize_keyI(Object _sample, long cdr){
        PlayerMove sample = (PlayerMove)_sample;
        return 0;
    }

    public int deserialize_keyI(Object _sample, long cdr) {
        PlayerMove sample = (PlayerMove)_sample;
        return 0;
    }

    public TypeCode get_typecode(){
        if (s_typeCode != null) {
            return s_typeCode;
        }
        TypeCodeFactory factory = TypeCodeFactory.get_instance();

        s_typeCode = factory.create_struct_TC("SnakeGame.PlayerMove");
        if (s_typeCode == null){
            System.out.println("create struct PlayerMove typecode failed.");
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

        memberTc = factory.create_string_TC(255);
        if (memberTc == null){
            System.out.println("Get Member direction TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            1,
            1,
            "direction",
            memberTc,
            false,
            false);
        factory.delete_TC(memberTc);
        if (ret < 0)
        {
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }

        memberTc = factory.get_primitive_TC(TypeCodeKind.DDS_TK_INT);
        if (memberTc == null){
            System.out.println("Get Member timestamp TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            2,
            2,
            "timestamp",
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