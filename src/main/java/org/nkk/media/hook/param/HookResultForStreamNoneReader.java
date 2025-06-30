package org.nkk.media.hook.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class HookResultForStreamNoneReader extends HookResult {

    private boolean close;

    public static HookResultForStreamNoneReader SUCCESS() {
        return new HookResultForStreamNoneReader(0, "success");
    }

    public static HookResultForStreamNoneReader CLOSE() {
        return new HookResultForStreamNoneReader(1, "success");
    }


    public HookResultForStreamNoneReader(int code, String msg) {
        setCode(code);
        setMsg(msg);
    }
}
