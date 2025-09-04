package com.example.snakegame.SnakeGame;

import com.zrdds.infrastructure.CDRDeserializer;
import com.zrdds.infrastructure.CDRSerializer;
import com.zrdds.infrastructure.TypeCode;
import com.zrdds.infrastructure.TypeCodeFactory;
import com.zrdds.infrastructure.TypeCodeImpl;
import com.zrdds.publication.DataWriter;
import com.zrdds.subscription.DataReader;
import com.zrdds.topic.TypeSupport;

import java.io.UnsupportedEncodingException;

public class LeaderboardTypeSupport extends TypeSupport {
    private String type_name = "Leaderboard";
    private static TypeCodeImpl s_typeCode = null;
    private static LeaderboardTypeSupport m_instance = new LeaderboardTypeSupport();

    private final byte[] tmp_byte_obj = new byte[1];
    private final char[] tmp_char_obj = new char[1];
    private final short[] tmp_short_obj = new short[1];
    private final int[] tmp_int_obj = new int[1];
    private final long[] tmp_long_obj = new long[1];
    private final float[] tmp_float_obj = new float[1];
    private final double[] tmp_double_obj = new double[1];
    private final boolean[] tmp_boolean_obj = new boolean[1];

    
    private LeaderboardTypeSupport(){}

    
    public static TypeSupport get_instance() { return m_instance; }

    public Object create_sampleI() {
        Leaderboard sample = new Leaderboard();
        return sample;
    }

    public void destroy_sampleI(Object sample) {

    }

    public int copy_sampleI(Object dst,Object src) {
        Leaderboard LeaderboardDst = (Leaderboard)dst;
        Leaderboard LeaderboardSrc = (Leaderboard)src;
        LeaderboardDst.copy(LeaderboardSrc);
        return 1;
    }

    public int print_sample(Object _sample) {
        if (_sample == null){
            System.out.println("NULL");
            return -1;
        }
        Leaderboard sample = (Leaderboard)_sample;
        int entriesTmpLen = sample.entries.length();
        System.out.println("sample.entries.length():" +entriesTmpLen);
        for (int i = 0; i < entriesTmpLen; ++i){
            LeaderboardEntryTypeSupport.get_instance().print_sample(sample.entries.get_at(i));
        }
        return 0;
    }

    public String get_type_name(){
        return this.type_name;
    }

    public int get_max_sizeI(){
        return 68344;
    }

    public int get_max_key_sizeI(){
        return 68344;
    }

    public boolean has_keyI(){
        return false;
    }

    public String get_keyhashI(Object sample, long cdr){
        return "-1";
    }

    public DataReader create_data_reader() {return new LeaderboardDataReader();}

    public DataWriter create_data_writer() {return new LeaderboardDataWriter();}

    public TypeCode get_inner_typecode(){
        TypeCode userTypeCode = get_typecode();
        if (userTypeCode == null) return null;
        return userTypeCode.get_impl();
    }

    public int get_sizeI(Object _sample,long cdr, int offset) throws UnsupportedEncodingException {
        int initialAlignment = offset;
        Leaderboard sample = (Leaderboard)_sample;
        offset += CDRSerializer.get_untype_size(4, offset);
        int entriesLen = sample.entries.length();
        if (entriesLen != 0){
            for (int i = 0; i < entriesLen; ++i){
                LeaderboardEntry curEle = sample.entries.get_at(i);
                offset += LeaderboardEntryTypeSupport.get_instance().get_sizeI(curEle, cdr, offset);
            }
        }

        return offset - initialAlignment;
    }

    public int serializeI(Object _sample ,long cdr) {
         Leaderboard sample = (Leaderboard) _sample;

        if (!CDRSerializer.put_int(cdr, sample.entries.length())){
            System.out.println("serialize length of sample.entries failed.");
            return -2;
        }
        for (int i = 0; i < sample.entries.length(); ++i){
            if (LeaderboardEntryTypeSupport.get_instance().serializeI(sample.entries.get_at(i),cdr) < 0){
                System.out.println("serialize sample.entriesfailed.");
                return -2;
            }
        }

        return 0;
    }

    synchronized public int deserializeI(Object _sample, long cdr){
        Leaderboard sample = (Leaderboard) _sample;
        if (!CDRDeserializer.get_int_array(cdr, tmp_int_obj, 1)){
            System.out.println("deserialize length of sample.entries failed.");
            return -2;
        }
        if (!sample.entries.ensure_length(tmp_int_obj[0], tmp_int_obj[0])){
            System.out.println("Set maxiumum member sample.entries failed.");
            return -3;
        }
        LeaderboardEntry tmpentries = new LeaderboardEntry();
        for (int i = 0; i < sample.entries.length(); ++i){
            if (LeaderboardEntryTypeSupport.get_instance().deserializeI(tmpentries, cdr) < 0){
                System.out.println("deserialize sample.entries failed.");
                return -2;
            }
            sample.entries.set_at(i, tmpentries);
        }

        return 0;
    }

    public int get_key_sizeI(Object _sample,long cdr,int offset)throws UnsupportedEncodingException {
        int initialAlignment = offset;
        Leaderboard sample = (Leaderboard)_sample;
        offset += get_sizeI(sample, cdr, offset);
        return offset - initialAlignment;
    }

    public int serialize_keyI(Object _sample, long cdr){
        Leaderboard sample = (Leaderboard)_sample;
        return 0;
    }

    public int deserialize_keyI(Object _sample, long cdr) {
        Leaderboard sample = (Leaderboard)_sample;
        return 0;
    }

    public TypeCode get_typecode(){
        if (s_typeCode != null) {
            return s_typeCode;
        }
        TypeCodeFactory factory = TypeCodeFactory.get_instance();

        s_typeCode = factory.create_struct_TC("SnakeGame.Leaderboard");
        if (s_typeCode == null){
            System.out.println("create struct Leaderboard typecode failed.");
            return s_typeCode;
        }
        int ret = 0;
        TypeCodeImpl memberTc = new TypeCodeImpl();
        TypeCodeImpl eleTc = new TypeCodeImpl();

        memberTc = (TypeCodeImpl) LeaderboardEntryTypeSupport.get_instance().get_typecode();
        if (memberTc != null)
        {
            memberTc = factory.create_sequence_TC(255, memberTc);
        }
        if (memberTc == null){
            System.out.println("Get Member entries TypeCode failed.");
            factory.delete_TC(s_typeCode);
            s_typeCode = null;
            return null;
        }
        ret = s_typeCode.add_member_to_struct(
            0,
            0,
            "entries",
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