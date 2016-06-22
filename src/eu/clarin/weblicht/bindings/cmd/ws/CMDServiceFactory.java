package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.StringBinding;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author akislev
 */
public class CMDServiceFactory {

    public static InputParameter newInputParameter(String name, String arg, boolean required) {
        return new InputParameter(new StringBinding(name), new StringBinding(arg), required);
    }

    public static InputValue newInputValue(String name, String arg) {
        StringBinding argBiniding = (arg == null || arg.isEmpty()) ? null : new StringBinding(arg);
        return new InputValue(new StringBinding(name), argBiniding);
    }

    public static OutputParameter newOutputParameter(String name, String ref) {
        StringBinding refBiniding = (ref == null || ref.isEmpty()) ? null : new StringBinding(ref);
        return new OutputParameter(new StringBinding(name), refBiniding);
    }

    public static OutputValue newOutputValue(String name, Collection<String> refs) {
        List<StringBinding> bindings = null;
        if (refs != null) {
            bindings = new ArrayList<StringBinding>(refs.size());
            for (String ref : refs) {
                bindings.add(new StringBinding(ref));
            }
        }
        return new OutputValue(new StringBinding(name), bindings);
    }

    public static ServiceCMD newServiceCMD(URI wadlLocation) {
        return new ServiceCMD(wadlLocation);
    }
}
