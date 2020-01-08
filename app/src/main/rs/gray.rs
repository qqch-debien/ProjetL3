# pragma version (1)
# pragma rs java_package_name (com.example.modif_image)


uchar4 RS_KERNEL toGray(uchar4 in){

    float4 pixelf = rsUnpackColor8888(in);

    float gray = (0.299f*pixelf.r +
                  0.587f*pixelf.g +
                  0.114f*pixelf.b);

    return rsPackColorTo8888(gray , gray , gray , pixelf.a);

}