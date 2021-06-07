package org.lc.fe.exception;

public class ColumnDuplicateException extends Exception{

    private static final long serialVersionUID = 8231747940799678649L;

    public ColumnDuplicateException(String msg) {
        super(msg);
    }
}
