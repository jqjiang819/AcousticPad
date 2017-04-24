% init
clear;
flags.algorithm = 'LEVD';
vars.cod.time = 0;
vars.cic.dbuf_i = zeros(17,3);
vars.cic.dbuf_q = zeros(17,3);
vars.cic.ibuf_i = zeros(1,3);
vars.cic.ibuf_q = zeros(1,3);
vars.levd.ext_i = utilities.initext();
vars.levd.s_init_i = 0;
vars.levd.ext_q = utilities.initext();
vars.levd.s_init_q = 0;
s_i = [];
s_q = [];
vec_i = [];
vec_q = [];
DATA_DBG_i = [];
DATA_DBG_q = [];
global DATA_DBG_EXTS;
DATA_DBG_EXTS.v = [];
DATA_DBG_EXTS.i = [];
DATA_DBG_EXTS.cnt = 0;
% get audio data
snd_data = sndcnvrt('../res/record.pcm');
tic;
for i = 1:size(snd_data,2)
    % coherent detection
    [data_i,data_q,vars.cod.time] = codetect(snd_data(:,i),vars.cod.time);
    % cic filtering
    [data_cic_i,vars.cic.dbuf_i,vars.cic.ibuf_i] ...
            = cicdecim(data_i,vars.cic.dbuf_i,vars.cic.ibuf_i);
    [data_cic_q,vars.cic.dbuf_q,vars.cic.ibuf_q] ...
            = cicdecim(data_q,vars.cic.dbuf_q,vars.cic.ibuf_q);
    DATA_DBG_i = [DATA_DBG_i;data_cic_i];
    DATA_DBG_q = [DATA_DBG_q;data_cic_q];
    if strcmp(flags.algorithm,'LEVD')
        % levd detection
        [s_tmp_i,vars.levd.ext_i] = levddetect(data_cic_i,vars.levd.s_init_i,vars.levd.ext_i);
        vars.levd.s_init_i = s_tmp_i(end);
        [s_tmp_q,vars.levd.ext_q] = levddetect(data_cic_q,vars.levd.s_init_q,vars.levd.ext_q);
        vars.levd.s_init_q = s_tmp_q(end);
        s_i = [s_i;s_tmp_i];
        s_q = [s_q;s_tmp_q];
    elseif strcmp(flags.algorithm,'FCD')
        % fcd detection
        [s_tmp_i,s_tmp_q] = fcddetect(data_cic_i, data_cic_q);
        s_i = [s_i;s_tmp_i];
        s_q = [s_q;s_tmp_q];
    else
        error('Algorithm "%s" not exist', flags.algorithm);
    end
    % calculation
    vec_tmp_i = data_cic_i - s_tmp_i;
    vec_tmp_q = data_cic_q - s_tmp_q;
    vec_i = [vec_i;vec_tmp_i];
    vec_q = [vec_q;vec_tmp_q];
end
toc;
% test
pha = phase(vec_q+1j*vec_i);
len = pha/(2*pi)*1.8889;
len = (len-len(1))/2;
t=1/3000:1/3000:length(len)/3000;
plot(t,len);