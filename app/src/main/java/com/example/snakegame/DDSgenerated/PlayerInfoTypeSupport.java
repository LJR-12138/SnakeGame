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

public class PlayerInfoTypeSupport extends TypeSupport {
    private String type_name = "PlayerInfo";
    private static TypeCodeImpl s_typeCode = null;
    private static PlayerInfoTypeSupport m_instance = new PlayerInfoTypeSupport();

    private final byte[] tmp_byte_obj = new byte[1];
    private final char[] tmp_char_obj = new char[1];
    private final short[] tmp_short_obj = new short[1];
    private final int[] tmp_int_obj = new int[1];
    private final long[] tmp_long_obj = new long[1];
    private final float[] tmp_float_obj = new float[1];
    private final double[] tmp_double_obj = new double[1];
    private final boolean[] tmp_boolean_obj = new boolean[1];

    
    private PlayerInfoTypeSupport(){}

    
    public static TypeSupport get_instance() { return m_instance; }

    public Object create_sampleI() {
        PlayerInfo sample = new PlayerInfo();
        return sample;
    }

    public void destroy_sampleI(Object sample) {

    }

    public int copy_sampleI(Object dst,Object src) {
        PlayerInfo PlayerInfoDst = (PlayerInfo)dst;
        PlayerInfo PlayerInfoSrc = (PlayerInfo)src;
        PlayerInfoDst.copy(PlayerInfoSrc);
        return 1;
    }

    public int print_sample(Object _sample) {
        if (_sample == null){
            System.out.println("NULL");
            return -1;
        }
        PlayerInfo sample = (PlayerInfo)_sample;
        System.out.println("sample.player_id:" + sample.player_id);
        if (sample.nickname != null){
            System.out.println("sample.nickname:" + sample.nickname);
        }
        else{
            System.out.println("sample.nickname: null");
        }
        if (sample.color != null){
            System.out.println("sample.color:" + sample.color);
        }
        else{
            System.out.println("sample.color: null");
        }
        return 0;
    }

    public String get_type_name(){
        return this.type_name;
    }

    public int get_max_sizeI(){
        return 524;
    }

    public int get_max_key_sizeI(){
        return 524;
    }

    public boolean has_keyI(){
        return false;
    }

    public String get_keyhashI(Object sample, long cdr){
        return "-1";
    }

    public DataReader create_data_reader() {return new PlayerInfoDataReader();}

    public DataWriter create_data_writer() {return new PlayerInfoDataWriter();}

    public TypeCode get_inner_typecode(){
        TypeCode userTypeCode = get_typecode();
        if (userTypeCode == null) return null;
        return userTypeCode.get_impl();
    }

    public int get_sizeI(Object _sample,long cdr, int offset) throws UnsupportedEncodingException {
        int initialAlignment = offset;
        PlayerInfo sample = (PlayerInfo)_sample;
        offset += CDRSerializer.get_untype_size(4, offset);

        offset += CDRSerializer.get_string_size(sample.nickname == null ? 0 : sample.nickname.getBytes().length, offset);

        offset += CDRSerializer.get_string_size(sample.color == null ? 0 : sample.color.getBytes().length, offset);

        return offset - initialAlignment;
    }

    public int serializeI(Object _sample ,long cdr) {
         PlayerInfo sample = (PlayerInfo) _sample;

        if (!CDRSerializer.put_int(cdr, sample.player_id)){
            System.out.println("serialize sample.player_id failed.");
            return -2;
        }

        if (!CDRSerializer.put_string(cdr, sample.nickname, sample.nickname == null ? 0 : sample.nickname.length())){
            System.out.println("serialize sample.nickname failed.");
            return -2;
        }

        if (!CDRSerializer.put_string(cdr, sample.color, sample.color == null ? 0 : sample.color.length())){
            System.out.println("serialize sample.color failed.");
            return -2;
        }

        return 0;
    }

    synchronized public int deserializeI(Object _sample, long cdr){
        PlayerInfo sample = (PlayerInfo) _sample;
        if (!CDRDeserializer.get_int_array(cdr, tmp_int_obj, 1)){
            System.out.println("deserialize sample.player_id failed.");
            return -2;
        }
        sample.player_id= tmp_int_obj[0];

        sample.nickname = CDRDeserializer.get_string(cdr);
        if(sample.nickname ==null){
            System.out.println("deserialize member sample.nickname failed.");
            return -3;
        }

        sample.color = CDRDeserializer.get_string(cdr);
        if(sample.color ==null){
            System.out.println("deserialize member sample.color failed.");
            return -3;
        }

        return 0;
    }

    public int get_key_sizeI(Object _sample,long cdr,int offset)throws UnsupportedEncodingException {
        int initialAlignment = offset;
        PlayerInfo sample = (PlayerInfo)_sample;
        offset += get_sizeI(sample, cdr, offset);
        return offset - initialAlignment;
    }

    public int serialize_keyI(Object _sample, long cdr){
        PlayerInfo sample = (PlayerInfo)_sample;
        return 0;
    }

    public int deserialize_keyI(Object _sample, long cdr) {
        PlayerInfo sample = (PlayerInfo)_sample;
        return 0;
    }

    public TypeCode get_typecode(){
        if (s_typeCode != null) {
            return s_typeCode;
        }
        TypeCodeFactory factory = TypeCodeFactory.get_instance();

        s_typeCode = factory.create_struct_TC("SnakeGame.PlayerInfo");
        if (s_typeCode == null){
            System.out.println("create struct PlayerInfo typecode failed.");
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
            System.out.println("Get Member nickname TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            1,
            1,
            "nickname",
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

        memberTc = factory.create_string_TC(255);
        if (memberTc == null){
            System.out.println("Get Member color TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            2,
            2,
            "color",
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

        return s_typeCode;
    }

}