package enginecrafter77.survivalinc.net;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nullable;
import java.lang.annotation.*;

@Nullable
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface MessageHandler {
	Class<? extends IMessage> messageType() default IMessage.class;
}
