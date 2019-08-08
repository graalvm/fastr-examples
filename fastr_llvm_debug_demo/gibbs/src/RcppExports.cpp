// Generated by using Rcpp::compileAttributes() -> do not edit by hand
// Generator token: 10BE3573-1514-4C36-9D1C-5A225CD40393

#include <Rcpp.h>

using namespace Rcpp;

// gibbs_cpp
NumericMatrix gibbs_cpp(int N, int thin);
RcppExport SEXP _gibbs_gibbs_cpp(SEXP NSEXP, SEXP thinSEXP) {
BEGIN_RCPP
    Rcpp::RObject rcpp_result_gen;
    Rcpp::RNGScope rcpp_rngScope_gen;
    Rcpp::traits::input_parameter< int >::type N(NSEXP);
    Rcpp::traits::input_parameter< int >::type thin(thinSEXP);
    rcpp_result_gen = Rcpp::wrap(gibbs_cpp(N, thin));
    return rcpp_result_gen;
END_RCPP
}

static const R_CallMethodDef CallEntries[] = {
    {"_gibbs_gibbs_cpp", (DL_FUNC) &_gibbs_gibbs_cpp, 2},
    {NULL, NULL, 0}
};

RcppExport void R_init_gibbs(DllInfo *dll) {
    R_registerRoutines(dll, NULL, CallEntries, NULL, NULL);
    R_useDynamicSymbols(dll, FALSE);
}
