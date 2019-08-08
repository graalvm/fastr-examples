#include <R.h>
#include <Rdefines.h>

SEXP lapplyNative(SEXP list, SEXP fn, SEXP rho) {
    int n = length(list);
    SEXP R_fcall, ans;

    R_fcall = PROTECT(lang2(fn, R_NilValue));
    ans = PROTECT(allocVector(VECSXP, n));
    for(int i = 0; i < n; i++) {
        SETCADR(R_fcall, VECTOR_ELT(list, i));
        SET_VECTOR_ELT(ans, i, eval(R_fcall, rho));
    }
    setAttrib(ans, R_NamesSymbol, 
        getAttrib(list, R_NamesSymbol));
    UNPROTECT(2);   
    return ans;
}
