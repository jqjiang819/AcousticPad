% init
clear all;
vars.cod.time = 0;
vars.cic.dbuf_i = zeros(17,3);
vars.cic.dbuf_q = zeros(17,3);
vars.cic.ibuf_i = zeros(1,3);
vars.cic.ibuf_q = zeros(1,3);
vars.levd.ext = utilities.initext();
vars.levd.s_init = 0;
s_i = [];
s_q = [];
vec_i = [];
vec_q = [];
% get audio data
snd_data = sndcnvrt('../res/record.pcm');
for i = 1:size(snd_data,2)
    % coherent detection
    [data_i,data_q,vars.cod.time] = codetect(snd_data(:,i),vars.cod.time);
    % cic filtering
    [data_cic_i,vars.cic.dbuf_i,vars.cic.ibuf_i] ...
            = cicdecim(data_i,vars.cic.dbuf_i,vars.cic.ibuf_i);
    [data_cic_q,vars.cic.dbuf_q,vars.cic.ibuf_q] ...
            = cicdecim(data_q,vars.cic.dbuf_q,vars.cic.ibuf_q);
    % levd detection
    [s_i_tmp,vars.levd.ext] = levddetect(data_cic_i,vars.levd.s_init,vars.levd.ext);
    [s_q_tmp,vars.levd.ext] = levddetect(data_cic_q,vars.levd.s_init,vars.levd.ext);
    s_i = [s_i;s_i_tmp];
    s_q = [s_q;s_q_tmp];
    % calculation
    vec_i_tmp = data_cic_i - s_i_tmp;
    vec_q_tmp = data_cic_q - s_q_tmp;
    vec_i = [vec_i;vec_i_tmp];
    vec_q = [vec_q;vec_q_tmp];
end
% test
pha = phase(vec_i+1j*vec_q);
len = -pha/(2*pi)*1.8889;
len = len-len(1);
t=1/3000:1/3000:length(len)/3000;
plot(t,len);