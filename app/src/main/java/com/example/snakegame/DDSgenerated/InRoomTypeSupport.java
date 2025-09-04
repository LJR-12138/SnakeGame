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

public class InRoomTypeSupport extends TypeSupport {
    private String type_name = "InRoom";
    private static TypeCodeImpl s_typeCode = null;
    private static InRoomTypeSupport m_instance = new InRoomTypeSupport();

    private final byte[] tmp_byte_obj = new byte[1];
    private final char[] tmp_char_obj = new char[1];
    private final short[] tmp_short_obj = new short[1];
    private final int[] tmp_int_obj = new int[1];
    private final long[] tmp_long_obj = new long[1];
    private final float[] tmp_float_obj = new float[1];
    private final double[] tmp_double_obj = new double[1];
    private final boolean[] tmp_boolean_obj = new boolean[1];

    
    private InRoomTypeSupport(){}

    
    public static TypeSupport get_instance() { return m_instance; }

    public Object create_sampleI() {
        InRoom sample = new InRoom();
        return sample;
    }

    public void destroy_sampleI(Object sample) {

    }

    public int copy_sampleI(Object dst,Object src) {
        InRoom InRoomDst = (InRoom)dst;
        InRoom InRoomSrc = (InRoom)src;
        InRoomDst.copy(InRoomSrc);
        return 1;
    }

    public int print_sample(Object _sample) {
        if (_sample == null){
            System.out.println("NULL");
            return -1;
        }
        InRoom sample = (InRoom)_sample;
        if (sample.room_id != null){
            System.out.println("sample.room_id:" + sample.room_id);
        }
        else{
            System.out.println("sample.room_id: null");
        }
        System.out.println("sample.player_id:" + sample.player_id);
        if (sample.player_nickname != null){
            System.out.println("sample.player_nickname:" + sample.player_nickname);
        }
        else{
            System.out.println("sample.player_nickname: null");
        }
        int player_idsTmpLen = sample.player_ids.length();
        System.out.println("sample.player_ids.length():" +player_idsTmpLen);
        for (int i = 0; i < player_idsTmpLen; ++i){
            System.out.println("sample.player_ids.get_at(" + i + "):" + sample.player_ids.get_at(i));
        }
        int player_nicknamesTmpLen = sample.player_nicknames.length();
        System.out.println("sample.player_nicknames.length():" +player_nicknamesTmpLen);
        for (int i = 0; i < player_nicknamesTmpLen; ++i){
            if (sample.player_nicknames.get_at(i) != null){
                System.out.println("sample.player_nicknames.get_at(" + i + "):" + sample.player_nicknames.get_at(i));
            }
            else{
                System.out.println("sample.player_nicknames.get_at(" + i + "): null");
            }
        }
        if (sample.room_state != null){
            System.out.println("sample.room_state:" + sample.room_state);
        }
        else{
            System.out.println("sample.room_state: null");
        }
        return 0;
    }

    public String get_type_name(){
        return this.type_name;
    }

    public int get_max_sizeI(){
        return 68112;
    }

    public int get_max_key_sizeI(){
        return 68112;
    }

    public boolean has_keyI(){
        return false;
    }

    public String get_keyhashI(Object sample, long cdr){
        return "-1";
    }

    public DataReader create_data_reader() {return new InRoomDataReader();}

    public DataWriter create_data_writer() {return new InRoomDataWriter();}

    public TypeCode get_inner_typecode(){
        TypeCode userTypeCode = get_typecode();
        if (userTypeCode == null) return null;
        return userTypeCode.get_impl();
    }

    public int get_sizeI(Object _sample,long cdr, int offset) throws UnsupportedEncodingException {
        int initialAlignment = offset;
        InRoom sample = (InRoom)_sample;
        offset += CDRSerializer.get_string_size(sample.room_id == null ? 0 : sample.room_id.getBytes().length, offset);

        offset += CDRSerializer.get_untype_size(4, offset);

        offset += CDRSerializer.get_string_size(sample.player_nickname == null ? 0 : sample.player_nickname.getBytes().length, offset);

        offset += CDRSerializer.get_untype_size(4, offset);
        int player_idsLen = sample.player_ids.length();
        if (player_idsLen != 0){
            offset += 4 * player_idsLen;
        }

        offset += CDRSerializer.get_untype_size(4, offset);
        int player_nicknamesLen = sample.player_nicknames.length();
        if (player_nicknamesLen != 0){
            for(int i = 0; i<sample.player_nicknames.length(); ++i)
            {
                offset += CDRSerializer.get_string_size(sample.player_nicknames.get_at(i).getBytes().length,offset);
            }
        }

        offset += CDRSerializer.get_string_size(sample.room_state == null ? 0 : sample.room_state.getBytes().length, offset);

        return offset - initialAlignment;
    }

    public int serializeI(Object _sample ,long cdr) {
         InRoom sample = (InRoom) _sample;

        if (!CDRSerializer.put_string(cdr, sample.room_id, sample.room_id == null ? 0 : sample.room_id.length())){
            System.out.println("serialize sample.room_id failed.");
            return -2;
        }

        if (!CDRSerializer.put_int(cdr, sample.player_id)){
            System.out.println("serialize sample.player_id failed.");
            return -2;
        }

        if (!CDRSerializer.put_string(cdr, sample.player_nickname, sample.player_nickname == null ? 0 : sample.player_nickname.length())){
            System.out.println("serialize sample.player_nickname failed.");
            return -2;
        }

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

        if (!CDRSerializer.put_int(cdr, sample.player_nicknames.length())){
            System.out.println("serialize length of sample.player_nicknames failed.");
            return -2;
        }
        for (int i = 0; i < sample.player_nicknames.length(); ++i){
            if (!CDRSerializer.put_string(cdr, sample.player_nicknames.get_at(i), sample.player_nicknames.get_at(i) == null ? 0 : sample.player_nicknames.get_at(i).length())){
                System.out.println("serialize sample.player_nicknames failed.");
                return -2;
            }
        }

        if (!CDRSerializer.put_string(cdr, sample.room_state, sample.room_state == null ? 0 : sample.room_state.length())){
            System.out.println("serialize sample.room_state failed.");
            return -2;
        }

        return 0;
    }

    synchronized public int deserializeI(Object _sample, long cdr){
        InRoom sample = (InRoom) _sample;
        sample.room_id = CDRDeserializer.get_string(cdr);
        if(sample.room_id ==null){
            System.out.println("deserialize member sample.room_id failed.");
            return -3;
        }

        if (!CDRDeserializer.get_int_array(cdr, tmp_int_obj, 1)){
            System.out.println("deserialize sample.player_id failed.");
            return -2;
        }
        sample.player_id= tmp_int_obj[0];

        sample.player_nickname = CDRDeserializer.get_string(cdr);
        if(sample.player_nickname ==null){
            System.out.println("deserialize member sample.player_nickname failed.");
            return -3;
        }

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
            System.out.println("deserialize length of sample.player_nicknames failed.");
            return -2;
        }
        if (!sample.player_nicknames.ensure_length(tmp_int_obj[0], tmp_int_obj[0])){
            System.out.println("Set maxiumum member sample.player_nicknames failed.");
            return -3;
        }
        for(int i =0 ;i < sample.player_nicknames.length() ;++i)
        {
            sample.player_nicknames.set_at(i, CDRDeserializer.get_string(cdr));
        }

        sample.room_state = CDRDeserializer.get_string(cdr);
        if(sample.room_state ==null){
            System.out.println("deserialize member sample.room_state failed.");
            return -3;
        }

        return 0;
    }

    public int get_key_sizeI(Object _sample,long cdr,int offset)throws UnsupportedEncodingException {
        int initialAlignment = offset;
        InRoom sample = (InRoom)_sample;
        offset += get_sizeI(sample, cdr, offset);
        return offset - initialAlignment;
    }

    public int serialize_keyI(Object _sample, long cdr){
        InRoom sample = (InRoom)_sample;
        return 0;
    }

    public int deserialize_keyI(Object _sample, long cdr) {
        InRoom sample = (InRoom)_sample;
        return 0;
    }

    public TypeCode get_typecode(){
        if (s_typeCode != null) {
            return s_typeCode;
        }
        TypeCodeFactory factory = TypeCodeFactory.get_instance();

        s_typeCode = factory.create_struct_TC("SnakeGame.InRoom");
        if (s_typeCode == null){
            System.out.println("create struct InRoom typecode failed.");
            return s_typeCode;
        }
        int ret = 0;
        TypeCodeImpl memberTc = new TypeCodeImpl();
        TypeCodeImpl eleTc = new TypeCodeImpl();

        memberTc = factory.create_string_TC(255);
        if (memberTc == null){
            System.out.println("Get Member room_id TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            0,
            0,
            "room_id",
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
            System.out.println("Get Member player_id TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            1,
            1,
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
            System.out.println("Get Member player_nickname TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            2,
            2,
            "player_nickname",
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
            System.out.println("Get Member player_ids TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            3,
            3,
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

        memberTc = factory.create_string_TC(255);
        if (memberTc != null)
        {
            memberTc = factory.create_sequence_TC(255, memberTc);
        }
        if (memberTc == null){
            System.out.println("Get Member player_nicknames TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            4,
            4,
            "player_nicknames",
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
            System.out.println("Get Member room_state TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            5,
            5,
            "room_state",
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