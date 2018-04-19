package com.zj.sso.expection;

import javax.security.auth.login.LoginException;

/**
 * 无效用户，即被锁定了，状态不为1
 * @author zj
 *
 */
public class FailedStateException extends LoginException {
	  /**
     * Constructs a FailedLoginException with no detail message. A detail
     * message is a String that describes this particular exception.
     */
    public FailedStateException() {
        super();
    }

    /**
     * Constructs a FailedLoginException with the specified detail
     * message.  A detail message is a String that describes this particular
     * exception.
     *
     * <p>
     *
     * @param msg the detail message.
     */
    public FailedStateException(String msg) {
        super(msg);
    }

}
