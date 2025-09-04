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

public class CollisionTypeSupport extends TypeSupport {
    private String type_name = "Collision";
    private static TypeCodeImpl s_typeCode = null;
    private static CollisionTypeSupport m_instance = new CollisionTypeSupport();

    private final byte[] tmp_byte_obj = new byte[1];
    private final char[] tmp_char_obj = new char[1];
    private final short[] tmp_short_obj = new short[1];
    private final int[] tmp_int_obj = new int[1];
    private final long[] tmp_long_obj = new long[1];
    private final float[] tmp_float_obj = new float[1];
    private final double[] tmp_double_obj = new double[1];
    private final boolean[] tmp_boolean_obj = new boolean[1];

    
    private CollisionTypeSupport(){}

    
    public static TypeSupport get_instance() { return m_instance; }

    public Object create_sampleI() {
        Collision sample = new Collision();
        return sample;
    }

    public void destroy_sampleI(Object sample) {

    }

    public int copy_sampleI(Object dst,Object src) {
        Collision CollisionDst = (Collision)dst;
        Collision CollisionSrc = (Collision)src;
        CollisionDst.copy(CollisionSrc);
        return 1;
    }

    public int print_sample(Object _sample) {
        if (_sample == null){
            System.out.println("NULL");
            return -1;
        }
        Collision sample = (Collision)_sample;
        int player_idsTmpLen = sample.player_ids.length();
        System.out.println("sample.player_ids.length():" +player_idsTmpLen);
        for (int i = 0; i < player_idsTmpLen; ++i){
            System.out.println("sample.player_ids.get_at(" + i + "):" + sample.player_ids.get_at(i));
        }
        int collisionsTmpLen = sample.collisions.length();
        System.out.println("sample.collisions.length():" +collisionsTmpLen);
        for (int i = 0; i < collisionsTmpLen; ++i){
            System.out.println("sample.collisions.get_at(" + i + "):" + sample.collisions.get_at(i));
        }
        return 0;
    }

    public String get_type_name(){
        return this.type_name;
    }

    public int get_max_sizeI(){
        return 1283;
    }

    public int get_max_key_sizeI(){
        return 1283;
    }

    public boolean has_keyI(){
        return false;
    }

    public String get_keyhashI(Object sample, long cdr){
        return "-1";
    }

    public DataReader create_data_reader() {return new CollisionDataReader();}

    public DataWriter create_data_writer() {return new CollisionDataWriter();}

    public TypeCode get_inner_typecode(){
        TypeCode userTypeCode = get_typecode();
        if (userTypeCode == null) return null;
        return userTypeCode.get_impl();
    }

    public int get_sizeI(Object _sample,long cdr, int offset) throws UnsupportedEncodingException {
        int initialAlignment = offset;
        Collision sample = (Collision)_sample;
        offset += CDRSerializer.get_untype_size(4, offset);
        int player_idsLen = sample.player_ids.length();
        if (player_idsLen != 0){
            offset += 4 * player_idsLen;
        }

        offset += CDRSerializer.get_untype_size(4, offset);
        int collisionsLen = sample.collisions.length();
        if (collisionsLen != 0){
            offset += 1 * collisionsLen;
        }

        return offset - initialAlignment;
    }

    public int serializeI(Object _sample ,long cdr) {
         Collision sample = (Collision) _sample;

        if (!CDRSerializer.put_int(cdr, sample.player_ids.length())){
            System.out.println("serialize length of sample.player_ids failed.");
            return -2;
        }
        if (sample.player_ids.length() != 0){
            if (!CDRSerializer.put_int_array(cdr, sample.player_ids.get_contiguous_buffer(), sample.player_ids.length())){
                System.out.println("serialize sample.player_ids failed.");
                return -2;
            }
        }

        if (!CDRSerializer.put_int(cdr, sample.collisions.length())){
            System.out.println("serialize length of sample.collisions failed.");
            return -2;
        }
        if (sample.collisions.length() != 0){
            if (!CDRSerializer.put_boolean_array(cdr, sample.collisions.get_contiguous_buffer(), sample.collisions.length())){
                System.out.println("serialize sample.collisions failed.");
                return -2;
            }
        }

        return 0;
    }

    synchronized public int deserializeI(Object _sample, long cdr){
        Collision sample = (Collision) _sample;
        if (!CDRDeserializer.get_int_array(cdr, tmp_int_obj, 1)){
            System.out.println("deserialize length of sample.player_ids failed.");
            return -2;
        }
        if (!sample.player_ids.ensure_length(tmp_int_obj[0], tmp_int_obj[0])){
            System.out.println("Set maxiumum member sample.player_ids failed.");
            return -3;
        }
        if (!CDRDeserializer.get_int_array(cdr, sample.player_ids.get_contiguous_buffer(), sample.player_ids.length())){
            System.out.println("deserialize sample.player_ids failed.");
            return -2;
        }

        if (!CDRDeserializer.get_int_array(cdr, tmp_int_obj, 1)){
            System.out.println("deserialize length of sample.collisions failed.");
            return -2;
        }
        if (!sample.collisions.ensure_length(tmp_int_obj[0], tmp_int_obj[0])){
            System.out.println("Set maxiumum member sample.collisions failed.");
            return -3;
        }
        if (!CDRDeserializer.get_boolean_array(cdr, sample.collisions.get_contiguous_buffer(), sample.collisions.length())){
            System.out.println("deserialize sample.collisions failed.");
            return -2;
        }

        return 0;
    }

    public int get_key_sizeI(Object _sample,long cdr,int offset)throws UnsupportedEncodingException {
        int initialAlignment = offset;
        Collision sample = (Collision)_sample;
        offset += get_sizeI(sample, cdr, offset);
        return offset - initialAlignment;
    }

    public int serialize_keyI(Object _sample, long cdr){
        Collision sample = (Collision)_sample;
        return 0;
    }

    public int deserialize_keyI(Object _sample, long cdr) {
        Collision sample = (Collision)_sample;
        return 0;
    }

    public TypeCode get_typecode(){
        if (s_typeCode != null) {
            return s_typeCode;
        }
        TypeCodeFactory factory = TypeCodeFactory.get_instance();

        s_typeCode = factory.create_struct_TC("SnakeGame.Collision");
        if (s_typeCode == null){
            System.out.println("create struct Collision typecode failed.");
            return s_typeCode;
        }
        int ret = 0;
        TypeCodeImpl memberTc = new TypeCodeImpl();
        TypeCodeImpl eleTc = new TypeCodeImpl();

        memberTc = factory.get_primitive_TC(TypeCodeKind.DDS_TK_INT);
        if (memberTc != null)
        {
            memberTc = factory.create_sequence_TC(255, memberTc);
        }
        if (memberTc == null){
            System.out.println("Get Member player_ids TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            0,
            0,
            "player_ids",
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

        memberTc = factory.get_primitive_TC(TypeCodeKind.DDS_TK_BOOLEAN);
        if (memberTc != null)
        {
            memberTc = factory.create_sequence_TC(255, memberTc);
        }
        if (memberTc == null){
            System.out.println("Get Member collisions TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            1,
            1,
            "collisions",
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