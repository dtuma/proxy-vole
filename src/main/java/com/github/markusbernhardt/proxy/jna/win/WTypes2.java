package com.github.markusbernhardt.proxy.jna.win;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.ptr.ByReference;

/**
 * Pointer wrapper classes for various Windows SDK types. The JNA {@code WTypes} 
 * class already have a few of these, but oddly not for all.
 * 
 * <p>
 * TODO: Implement pointer wrapper classes for more WTypes, if and when needed.
 * 
 * @author phansson
 */
public class WTypes2 {
    
    private WTypes2() {}

    /**
     * A pointer to a LPWSTR.
     *
     * <p>
     * LPWSTR is itself a pointer, so a pointer to an LPWSTR is really a
     * pointer-to-pointer.
     *
     * <p>
     * The class is useful where the Windows function <i>returns</i> a result
     * into a variable of type {@code LPWSTR*}. The class currently has no
     * setters so it isn't useful for the opposite case, i.e. where a Windows
     * function <i>accepts</i> a {@code LPWSTR*} as its input.
     *
     *
     * @author phansson
     */
    public static class LPWSTRByReference extends ByReference {

        public LPWSTRByReference() {
            super(Native.POINTER_SIZE);
            // memory cleanup
            getPointer().setPointer(0, null);
        }

        /**
         * Gets the LPWSTR from this pointer. In general its a lot more
         * convenient simply to use {@link #getString() getString}. 
         * 
         * @return LPWSTR from this pointer
         */
        public WTypes.LPWSTR getValue() {
            Pointer p = getPointerToString();
            if (p == null) {
                return null;
            }
            WTypes.LPWSTR h = new WTypes.LPWSTR(p);
            return h;
        }

        /**
         * Gets the string as pointed to by the LPWSTR or {@code null} if
         * there's no LPWSTR.
         * 
         * @return LPWSTR from this pointer
         */
        public String getString() {
            WTypes.LPWSTR h = getValue();
            return h == null ? null : h.getValue();
        }

        public Pointer getPointerToString() {
			Pointer p = getPointer();
			if (p == null) {
				return null;
			}
            return p.getPointer(0);
        }
    }

}
