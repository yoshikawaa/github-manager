package io.github.yoshikawaa.app.githubmanager.core.thymeleaf;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

import org.springframework.util.StringUtils;

public class Bytes {

    private final Charset charset;

    public Bytes() {
        this(StandardCharsets.UTF_8);
    }

    public Bytes(Charset charset) {
        this.charset = charset;
    }
    
    public int length(String target) {
        return StringUtils.isEmpty(target) ? 0 : target.getBytes().length;
    }

    public String truncate(String target, int byteLength) {

        if (StringUtils.isEmpty(target)) {
            return target;
        }
        
        ByteBuffer bb = ByteBuffer.allocate(byteLength);
        CharBuffer cb = CharBuffer.wrap(target);
        CharsetEncoder encoder = charset.newEncoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE)
                .reset();
        CoderResult cr = encoder.encode(cb, bb, true);
        if (!cr.isOverflow()) {
            return target;
        }
        encoder.flush(bb);
        return cb.flip().toString();
    }

}
