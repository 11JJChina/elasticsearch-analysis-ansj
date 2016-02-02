package org.ansj.elasticsearch.action;

import org.ansj.domain.Term;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Streamable;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentBuilderString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhangqinghua on 16/2/2.
 */
public class AnsjResponse extends ActionResponse implements Iterable<AnsjResponse.AnsjTerm>, ToXContent {


    private List<AnsjTerm> terms = Collections.emptyList();

    public AnsjResponse(){}

    public AnsjResponse(List<Term> ts){
        this.terms = new ArrayList<>(ts.size());
        for(Term t: ts){
            this.terms.add(new AnsjTerm(t));
        }
    }



    public static class AnsjTerm implements Streamable {
        private String name;
        private String realName;
        private String nature;
        private int offset;

        AnsjTerm() {
        }

        public AnsjTerm(Term term) {
            this.name = term.getName();
            this.realName = term.getRealName();
            this.nature = term.getNatureStr();
            this.offset = term.getOffe();
        }

        public String getName() {
            return name;
        }

        public String getRealName() {
            return realName;
        }

        public String getNature() {
            return nature;
        }

        public int getOffset() {
            return offset;
        }

        public static AnsjTerm readAnsjTerm(StreamInput in) throws IOException {
            AnsjTerm analyzeToken = new AnsjTerm();
            analyzeToken.readFrom(in);
            return analyzeToken;
        }

        @Override
        public void readFrom(StreamInput in) throws IOException {
            name = in.readString();
            realName = in.readString();
            nature = in.readString();
            offset = in.readVInt();
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            out.writeString(name);
            out.writeString(realName);
            out.writeString(nature);
            out.writeVInt(offset);
        }
    }

    @Override
    public Iterator<AnsjTerm> iterator() {
        return terms.iterator();
    }

    public List<AnsjTerm> getTerms() {
        return terms;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startArray(Fields.TERMS);
        for (AnsjTerm term : terms) {
            builder.startObject();
            builder.field(Fields.NAME, term.getName());
            builder.field(Fields.REAL_NAME, term.getRealName());
            builder.field(Fields.NATURE, term.getNature());
            builder.field(Fields.OFFSET, term.getOffset());
            builder.endObject();
        }
        builder.endArray();
        return builder;
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
        int size = in.readVInt();
        terms = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            terms.add(AnsjTerm.readAnsjTerm(in));
        }
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeVInt(terms.size());
        for (AnsjTerm t : terms) {
            t.writeTo(out);
        }
    }

    static final class Fields {
        static final XContentBuilderString TERMS = new XContentBuilderString("terms");
        static final XContentBuilderString NAME = new XContentBuilderString("name");
        static final XContentBuilderString REAL_NAME = new XContentBuilderString("real_name");
        static final XContentBuilderString NATURE = new XContentBuilderString("nature");
        static final XContentBuilderString OFFSET = new XContentBuilderString("offset");
    }
}
