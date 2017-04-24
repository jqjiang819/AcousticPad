function [ data_out_i, data_out_q ] = fcddetect( data_in_i, data_in_q )
%FCDDETECT
% The frame-based center detection algorithm is a simple circular center
% fitting algorithm, which requires both the static vector and the movement
% speed of the target be static in the period of the frame. To perform this
% algorithm, the period of the frame should be as short as possible. This 
% algorithm utilizes the method of least squares to estimate the center 
% and thus obtain the value of the static component.

    fcd.frameSize = 3840;
    fcd.decf = 16;
    
    N = fcd.frameSize/fcd.decf;
    s_x = sum(data_in_i);
    s_y = sum(data_in_q);
    s_xx = sum(data_in_i.^2);
    s_xy = sum(data_in_i.*data_in_q);
    s_yy = sum(data_in_q.^2);
    s_xxx = sum(data_in_i.^3);
    s_xxy = sum((data_in_i.^2).*data_in_q);
    s_xyy = sum(data_in_i.*(data_in_q.^2));
    s_yyy = sum(data_in_q.^3);
    
    p1 = N*s_xx-s_x^2;
    p2 = N*s_xy-s_x*s_y;
    p3 = N*(s_xxx+s_xyy)-s_x*(s_xx+s_yy);
    p4 = N*s_xy-s_x*s_y;
    p5 = N*s_yy-s_y^2;
    p6 = N*(s_xxy+s_yyy)-s_y*(s_xx+s_yy);
    
    a = (p2*p6-p3*p5)/(p1*p5-p2*p4);
    b = (p3*p4-p1*p6)/(p1*p5-p2*p4);
    
    fcd.out_i = a/(-2);
    fcd.out_q = b/(-2);
    
    data_out_i = ones(N,1)*fcd.out_i;
    data_out_q = ones(N,1)*fcd.out_q;
    
end

function out = fcdvalidate(in)
%FCDVALIDATE
% valid: out = 1
% invalid: out = 0

end