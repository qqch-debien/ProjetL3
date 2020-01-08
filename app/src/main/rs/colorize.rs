# pragma version (1)
# pragma rs java_package_name (com.example.modif_image)

uchar rnd;

uchar4 RS_KERNEL colorize(uchar4 in){

    uchar4 out = in;

    out.r = rnd;

    return out;

}