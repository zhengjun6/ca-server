package com.zj.sso.expection;

import javax.security.auth.login.LoginException;

/**
 * 密码错误
 * @author zj
 *
 */
public class FailedPdException extends LoginException {
	  /**
     * Constructs a FailedLoginException with no detail message. A detail
     * message is a String that describes this particular exception.
     */
    public FailedPdException() {
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
    public FailedPdException(String msg) {
        super(msg);
    }

}
