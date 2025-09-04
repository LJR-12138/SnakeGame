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

public class GameStateTypeSupport extends TypeSupport {
    private String type_name = "GameState";
    private static TypeCodeImpl s_typeCode = null;
    private static GameStateTypeSupport m_instance = new GameStateTypeSupport();

    private final byte[] tmp_byte_obj = new byte[1];
    private final char[] tmp_char_obj = new char[1];
    private final short[] tmp_short_obj = new short[1];
    private final int[] tmp_int_obj = new int[1];
    private final long[] tmp_long_obj = new long[1];
    private final float[] tmp_float_obj = new float[1];
    private final double[] tmp_double_obj = new double[1];
    private final boolean[] tmp_boolean_obj = new boolean[1];

    
    private GameStateTypeSupport(){}

    
    public static TypeSupport get_instance() { return m_instance; }

    public Object create_sampleI() {
        GameState sample = new GameState();
        return sample;
    }

    public void destroy_sampleI(Object sample) {

    }

    public int copy_sampleI(Object dst,Object src) {
        GameState GameStateDst = (GameState)dst;
        GameState GameStateSrc = (GameState)src;
        GameStateDst.copy(GameStateSrc);
        return 1;
    }

    public int print_sample(Object _sample) {
        if (_sample == null){
            System.out.println("NULL");
            return -1;
        }
        GameState sample = (GameState)_sample;
        System.out.println("sample.player_id:" + sample.player_id);
        int snake_xTmpLen = sample.snake_x.length();
        System.out.println("sample.snake_x.length():" +snake_xTmpLen);
        for (int i = 0; i < snake_xTmpLen; ++i){
            System.out.println("sample.snake_x.get_at(" + i + "):" + sample.snake_x.get_at(i));
        }
        int snake_yTmpLen = sample.snake_y.length();
        System.out.println("sample.snake_y.length():" +snake_yTmpLen);
        for (int i = 0; i < snake_yTmpLen; ++i){
            System.out.println("sample.snake_y.get_at(" + i + "):" + sample.snake_y.get_at(i));
        }
        System.out.println("sample.length:" + sample.length);
        System.out.println("sample.score:" + sample.score);
        return 0;
    }

    public String get_type_name(){
        return this.type_name;
    }

    public int get_max_sizeI(){
        return 2060;
    }

    public int get_max_key_sizeI(){
        return 2060;
    }

    public boolean has_keyI(){
        return false;
    }

    public String get_keyhashI(Object sample, long cdr){
        return "-1";
    }

    public DataReader create_data_reader() {return new GameStateDataReader();}

    public DataWriter create_data_writer() {return new GameStateDataWriter();}

    public TypeCode get_inner_typecode(){
        TypeCode userTypeCode = get_typecode();
        if (userTypeCode == null) return null;
        return userTypeCode.get_impl();
    }

    public int get_sizeI(Object _sample,long cdr, int offset) throws UnsupportedEncodingException {
        int initialAlignment = offset;
        GameState sample = (GameState)_sample;
        offset += CDRSerializer.get_untype_size(4, offset);

        offset += CDRSerializer.get_untype_size(4, offset);
        int snake_xLen = sample.snake_x.length();
        if (snake_xLen != 0){
            offset += 4 * snake_xLen;
        }

        offset += CDRSerializer.get_untype_size(4, offset);
        int snake_yLen = sample.snake_y.length();
        if (snake_yLen != 0){
            offset += 4 * snake_yLen;
        }

        offset += CDRSerializer.get_untype_size(4, offset);

        offset += CDRSerializer.get_untype_size(4, offset);

        return offset - initialAlignment;
    }

    public int serializeI(Object _sample ,long cdr) {
         GameState sample = (GameState) _sample;

        if (!CDRSerializer.put_int(cdr, sample.player_id)){
            System.out.println("serialize sample.player_id failed.");
            return -2;
        }

        if (!CDRSerializer.put_int(cdr, sample.snake_x.length())){
            System.out.println("serialize length of sample.snake_x failed.");
            return -2;
        }
        if (sample.snake_x.length() != 0){
            if (!CDRSerializer.put_int_array(cdr, sample.snake_x.get_contiguous_buffer(), sample.snake_x.length())){
                System.out.println("serialize sample.snake_x failed.");
                return -2;
            }
        }

        if (!CDRSerializer.put_int(cdr, sample.snake_y.length())){
            System.out.println("serialize length of sample.snake_y failed.");
            return -2;
        }
        if (sample.snake_y.length() != 0){
            if (!CDRSerializer.put_int_array(cdr, sample.snake_y.get_contiguous_buffer(), sample.snake_y.length())){
                System.out.println("serialize sample.snake_y failed.");
                return -2;
            }
        }

        if (!CDRSerializer.put_int(cdr, sample.length)){
            System.out.println("serialize sample.length failed.");
            return -2;
        }

        if (!CDRSerializer.put_int(cdr, sample.score)){
            System.out.println("serialize sample.score failed.");
            return -2;
        }

        return 0;
    }

    synchronized public int deserializeI(Object _sample, long cdr){
        GameState sample = (GameState) _sample;
        if (!CDRDeserializer.get_int_array(cdr, tmp_int_obj, 1)){
            System.out.println("deserialize sample.player_id failed.");
            return -2;
        }
        sample.player_id= tmp_int_obj[0];

        if (!CDRDeserializer.get_int_array(cdr, tmp_int_obj, 1)){
            System.out.println("deserialize length of sample.snake_x failed.");
            return -2;
        }
        if (!sample.snake_x.ensure_length(tmp_int_obj[0], tmp_int_obj[0])){
            System.out.println("Set maxiumum member sample.snake_x failed.");
            return -3;
        }
        if (!CDRDeserializer.get_int_array(cdr, sample.snake_x.get_contiguous_buffer(), sample.snake_x.length())){
            System.out.println("deserialize sample.snake_x failed.");
            return -2;
        }

        if (!CDRDeserializer.get_int_array(cdr, tmp_int_obj, 1)){
            System.out.println("deserialize length of sample.snake_y failed.");
            return -2;
        }
        if (!sample.snake_y.ensure_length(tmp_int_obj[0], tmp_int_obj[0])){
            System.out.println("Set maxiumum member sample.snake_y failed.");
            return -3;
        }
        if (!CDRDeserializer.get_int_array(cdr, sample.snake_y.get_contiguous_buffer(), sample.snake_y.length())){
            System.out.println("deserialize sample.snake_y failed.");
            return -2;
        }

        if (!CDRDeserializer.get_int_array(cdr, tmp_int_obj, 1)){
            System.out.println("deserialize sample.length failed.");
            return -2;
        }
        sample.length= tmp_int_obj[0];

        if (!CDRDeserializer.get_int_array(cdr, tmp_int_obj, 1)){
            System.out.println("deserialize sample.score failed.");
            return -2;
        }
        sample.score= tmp_int_obj[0];

        return 0;
    }

    public int get_key_sizeI(Object _sample,long cdr,int offset)throws UnsupportedEncodingException {
        int initialAlignment = offset;
        GameState sample = (GameState)_sample;
        offset += get_sizeI(sample, cdr, offset);
        return offset - initialAlignment;
    }

    public int serialize_keyI(Object _sample, long cdr){
        GameState sample = (GameState)_sample;
        return 0;
    }

    public int deserialize_keyI(Object _sample, long cdr) {
        GameState sample = (GameState)_sample;
        return 0;
    }

    public TypeCode get_typecode(){
        if (s_typeCode != null) {
            return s_typeCode;
        }
        TypeCodeFactory factory = TypeCodeFactory.get_instance();

        s_typeCode = factory.create_struct_TC("SnakeGame.GameState");
        if (s_typeCode == null){
            System.out.println("create struct GameState typecode failed.");
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
        if (memberTc != null)
        {
            memberTc = factory.create_sequence_TC(255, memberTc);
        }
        if (memberTc == null){
            System.out.println("Get Member snake_x TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            1,
            1,
            "snake_x",
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
        if (memberTc != null)
        {
            memberTc = factory.create_sequence_TC(255, memberTc);
        }
        if (memberTc == null){
            System.out.println("Get Member snake_y TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            2,
            2,
            "snake_y",
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
            System.out.println("Get Member length TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            3,
            3,
            "length",
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
            System.out.println("Get Member score TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            4,
            4,
            "score",
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